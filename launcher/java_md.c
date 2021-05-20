/*
 * @(#)java_md.c	1.38 05/11/29
 *
 * Copyright 2006 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Backported from Tiger (1.5) java_md.c	1.43 03/11/03
 *
 * Support for amd64 removed.
 * Much, if not all, of:
 *      4884169: RFE: JVM could adapt to the class of machine it's on
 * removed or disabled.
 */

#include "java.h"
#include <dirent.h>
#include <dlfcn.h>
#include <fcntl.h>
#include <stdio.h>
#include <string.h>
#include <stdlib.h>
#include <limits.h>
#include <sys/stat.h>
#include <unistd.h>
#include <sys/types.h>
#include "manifest_info.h"
#include "version_comp.h"

#ifdef DEBUG
#define JVM_DLL "libjvm_g.so"
#define JAVA_DLL "libjava_g.so"
#else
#define JVM_DLL "libjvm.so"
#define JAVA_DLL "libjava.so"
#endif

/*
 * If a processor / os combination has the ability to run binaries of
 * with two data models (e.g. Solaris) then
 * DUAL_MODE is defined.  When DUAL_MODE is defined, the architecture
 * names for the narrow and wide version of the architecture are
 * defined in BIG_ARCH and SMALL_ARCH.
 */

#ifdef _LP64

#  ifdef ia64
#    define ARCH "ia64"
#  elif defined(amd64)
#    define ARCH "amd64"
#  elif defined(__sparc)
#    define ARCH "sparcv9"
#  else
#    define ARCH "unknown" /* unknown 64-bit architecture */
#  endif

#else /* 32-bit data model */

#  ifdef i586
#    define ARCH "i386"
#  elif defined(__sparc)
#    define ARCH "sparc"
#  endif

#endif /* _LP64 */

#ifdef __sun
#  define DUAL_MODE
#  ifdef __sparc
#    define BIG_ARCH "sparcv9"
#    define SMALL_ARCH "sparc"
#  else
#    define BIG_ARCH "amd64"
#    define SMALL_ARCH "i386"
#  endif
#  include <sys/systeminfo.h>
#  include <sys/elf.h>
#  include <stdio.h>
#else
#  ifndef ARCH
#    include <sys/systeminfo.h>
#  endif
#endif

/* pointer to environment */
extern char **environ;

/*
 *      A collection of useful strings. One should think of these as #define
 *      entries, but actual strings can be more efficient (with many compilers).
 */
#ifdef __linux__
static const char *system_dir	= "/usr/java";
static const char *user_dir	= "/java";
#else /* Solaris */
static const char *system_dir	= "/usr/jdk";
static const char *user_dir	= "/jdk";
#endif

/*
 * Flowchart of launcher execs and options processing on unix
 *
 * The selection of the proper vm shared library to open depends on
 * several classes of command line options, including vm "flavor"
 * options (-client, -server) and the data model options, -d32  and
 * -d64, as well as a version specification which may have come from
 * the command line or from the manifest of an executable jar file.
 * The vm selection options are not passed to the running
 * virtual machine; they must be screened out by the launcher.
 *
 * The version specification (if any) is processed first by the
 * platform independent routine SelectVersion.  This may result in
 * the exec of the specified launcher version.
 *
 * Typically, the launcher execs at least once to ensure a suitable
 * LD_LIBRARY_PATH is in effect for the process.  The first exec
 * screens out all the data model options; leaving the choice of data
 * model implicit in the binary selected to run.  However, in case no
 * exec is done, the data model options are screened out before the vm
 * is invoked.
 *
 *  incoming argv ------------------------------
 *  |                                          |
 * \|/                                         |
 * CheckJVMType                                |
 * (removes -client, -server, etc.)            |
 *                                            \|/
 *                                            CreateExecutionEnvironment
 *                                            (removes -d32 and -d64, 
 *                                             determines desired data model,
 *                                             sets up LD_LIBRARY_PATH, 
 *                                             and exec's)
 *                                             |
 *  --------------------------------------------
 *  |
 * \|/
 * exec child 1 incoming argv -----------------
 *  |                                          |
 * \|/                                         |
 * CheckJVMType                                |
 * (removes -client, -server, etc.)            |
 *  |                                         \|/
 *  |                                          CreateExecutionEnvironment
 *  |                                          (verifies desired data model 
 *  |                                           is running and acceptable 
 *  |                                           LD_LIBRARY_PATH;
 *  |                                           no-op in child)
 *  |
 * \|/
 * TranslateDashJArgs...
 * (Prepare to pass args to vm)
 *  |
 *  |
 *  |
 * \|/
 * ParseArguments
 * (ignores -d32 and -d64,
 *  processes version options,
 *  creates argument list for vm, 
 *  etc.)
 * 
 */

static char *SetExecname(char **argv);
static char * GetExecname();
static jboolean GetJVMPath(const char *jrepath, const char *jvmtype,
			   char *jvmpath, jint jvmpathsize, char * arch);
static jboolean GetJREPath(char *path, jint pathsize, char * arch, jboolean speculative);

const char *
GetArch()
{
    static char *arch = NULL;
    static char buf[12];
    if (arch) {
	return arch;
    }

#ifdef ARCH
    strcpy(buf, ARCH);
#else
    sysinfo(SI_ARCHITECTURE, buf, sizeof(buf));
#endif
    arch = buf;
    return arch;
}

void
CreateExecutionEnvironment(int *_argcp,
			   char ***_argvp,
			   char jrepath[],
			   jint so_jrepath,
			   char jvmpath[],
			   jint so_jvmpath,
			   char **original_argv) {
  /*
   * First, determine if we are running the desired data model.  If we
   * are running the desired data model, all the error messages
   * associated with calling GetJREPath, ReadKnownVMs, etc. should be
   * output.  However, if we are not running the desired data model,
   * some of the errors should be surpressed since it is more
   * infomrative to issue an error message based on whether or not the
   * os/processor combination has dual mode capabilities.
   */

    char *execname = NULL;
    int original_argc = *_argcp;
    jboolean jvmpathExists;

    /* Compute the name of the executable */
    execname = SetExecname(*_argvp);

    /* Set the LD_LIBRARY_PATH environment variable, check data model
       flags, and exec process, if needed */
    {
      char *arch	= (char *)GetArch(); /* like sparc or sparcv9 */
      char * jvmtype 	= NULL;
      int argc		= *_argcp;
      char **argv	= original_argv;

      char *runpath	= NULL; /* existing effective LD_LIBRARY_PATH
				   setting */

      int running	=	/* What data model is being ILP32 =>
				   32 bit vm; LP64 => 64 bit vm */
#ifdef _LP64 
	64;
#else
	32;
#endif

      int wanted	= running;	/* What data mode is being
					   asked for? Current model is
					   fine unless another model
					   is asked for */

      char* new_runpath	= NULL; /* desired new LD_LIBRARY_PATH string */
      char* newpath	= NULL; /* path on new LD_LIBRARY_PATH */
      char* lastslash	= NULL;

      char** newenvp	= NULL; /* current environment */

#ifdef __sun
      char** newargv	= NULL;
      int    newargc	= 0;
      char*  dmpath	= NULL;  /* data model specific LD_LIBRARY_PATH,
				    Solaris only */
#endif    

      /*
       * Starting in 1.5, all unix platforms accept the -d32 and -d64
       * options.  On platforms where only one data-model is supported
       * (e.g. ia-64 Linux), using the flag for the other data model is
       * an error and will terminate the program.
       *
       * But that is the future (and the source for this backport).
       * Pre-1.5, Solaris is the only supported platform which
       * accepts the -d32 and -d64 options since it is the only
       * supported platform that allows running either 32 or 64 bit
       * binaries.  If other such platforms are added in the future
       * (Linux on Hammer?), the #ifdef below will have to be adjusted
       * accordingly.
       */

#ifdef __sun
      { /* open new scope to declare local variables */
	int i;

	newargv = (char **)MemAlloc((argc+1) * sizeof(*newargv));
	newargv[newargc++] = argv[0];

	/* scan for data model arguments and remove from argument list;
	   last occurrence determines desired data model */
	for (i=1; i < argc; i++) {

	  if (strcmp(argv[i], "-J-d64") == 0 || strcmp(argv[i], "-d64") == 0) {
	    wanted = 64;
	    continue;
	  }
	  if (strcmp(argv[i], "-J-d32") == 0 || strcmp(argv[i], "-d32") == 0) {
	    wanted = 32;
	    continue;
	  }
	  newargv[newargc++] = argv[i];

#ifdef JAVA_ARGS
	  if (argv[i][0] != '-')
	    continue;
#else
	  if (strcmp(argv[i], "-classpath") == 0 || strcmp(argv[i], "-cp") == 0) {
	    i++;
	    if (i >= argc) break;
	    newargv[newargc++] = argv[i];
	    continue;
	  }
	  if (argv[i][0] != '-') { i++; break; }
#endif
	}

	/* copy rest of args [i .. argc) */
	while (i < argc) {
	  newargv[newargc++] = argv[i++];
	}
	newargv[newargc] = NULL;

	/* 
	 * newargv has all proper arguments here
	 */
    
	argc = newargc;
	argv = newargv;
      }
#endif /* end of __sun */

      /* If the data model is not changing, it is an error if the
	 jvmpath does not exist */
      if (wanted == running) {
	/* Find out where the JRE is that we will be using. */
	if (!GetJREPath(jrepath, so_jrepath, arch, JNI_FALSE) ) {
	  fprintf(stderr, "Error: could not find Java 2 Runtime Environment.\n");
	  exit(2);
	}

	/* Find the specified JVM type */
	if (ReadKnownVMs(jrepath, arch, JNI_FALSE) < 1) {
	  fprintf(stderr, "Error: no known VMs. (check for corrupt jvm.cfg file)\n");
	  exit(1);
	}

	jvmpath[0] = '\0';
	jvmtype = CheckJvmType(_argcp, _argvp, JNI_FALSE);

	if (!GetJVMPath(jrepath, jvmtype, jvmpath, so_jvmpath, arch )) {
	  fprintf(stderr, "Error: no `%s' JVM at `%s'.\n", jvmtype, jvmpath);
	  exit(4);
	}
      } else {  /* do the same speculatively or exit */
#ifdef DUAL_MODE
	if (running != wanted) {
	  /* Find out where the JRE is that we will be using. */
	  if (!GetJREPath(jrepath, so_jrepath, ((wanted==64)?BIG_ARCH:SMALL_ARCH), JNI_TRUE)) {
	    goto EndDataModelSpeculate;
	  }

	  /*
	   * Read in jvm.cfg for target data model and process vm
	   * selection options.
	   */
	  if (ReadKnownVMs(jrepath, ((wanted==64)?BIG_ARCH:SMALL_ARCH), JNI_TRUE) < 1) {
	    goto EndDataModelSpeculate;
	  }
	  jvmpath[0] = '\0';
	  jvmtype = CheckJvmType(_argcp, _argvp, JNI_TRUE);
	  /* exec child can do error checking on the existence of the path */
	  jvmpathExists = GetJVMPath(jrepath, jvmtype, jvmpath, so_jvmpath, 
				     ((wanted==64)?BIG_ARCH:SMALL_ARCH));

	}
      EndDataModelSpeculate: /* give up and let other code report error message */
	;
#else
	fprintf(stderr, "Running a %d-bit JVM is not supported on this platform.\n", wanted);
	exit(1);
#endif
      }

      /*
       * We will set the LD_LIBRARY_PATH as follows:
       *
       *     o		$JVMPATH (directory portion only)
       *     o		$JRE/lib/$ARCH
       *     o		$JRE/../lib/$ARCH
       *
       * followed by the user's previous effective LD_LIBRARY_PATH, if
       * any.
       */

#ifdef __sun
      /* 
       * Starting in Solaris 7, ld.so.1 supports three LD_LIBRARY_PATH
       * variables:
       *
       * 1. LD_LIBRARY_PATH -- used for 32 and 64 bit searches if
       * data-model specific variables are not set.
       *
       * 2. LD_LIBRARY_PATH_64 -- overrides and replaces LD_LIBRARY_PATH
       * for 64-bit binaries.
       *
       * 3. LD_LIBRARY_PATH_32 -- overrides and replaces LD_LIBRARY_PATH
       * for 32-bit binaries.
       *
       * The vm uses LD_LIBRARY_PATH to set the java.library.path system
       * property.  To shield the vm from the complication of multiple
       * LD_LIBRARY_PATH variables, if the appropriate data model
       * specific variable is set, we will act as if LD_LIBRARY_PATH had
       * the value of the data model specific variant and the data model
       * specific variant will be unset.  Note that the variable for the
       * *wanted* data model must be used (if it is set), not simply the
       * current running data model.
       */

      switch(wanted) {
      case 0:
	if(running == 32) {
	  dmpath = getenv("LD_LIBRARY_PATH_32");
	  wanted = 32;
	}
	else {
	  dmpath = getenv("LD_LIBRARY_PATH_64");
	  wanted = 64;
	}
	break;

      case 32:
	dmpath = getenv("LD_LIBRARY_PATH_32");
	break;

      case 64:
	dmpath = getenv("LD_LIBRARY_PATH_64");
	break;
      
      default:
	fprintf(stderr, "Improper value at line %d.", __LINE__);
	exit(1); /* unknown value in wanted */
	break;
      }
    
      /* 
       * If dmpath is NULL, the relevant data model specific variable is
       * not set and normal LD_LIBRARY_PATH should be used.
       */
      if( dmpath == NULL) {
	runpath = getenv("LD_LIBRARY_PATH");
      }
      else {
	runpath = dmpath;
      }
#else
      /*
       * If not on Solaris, assume only a single LD_LIBRARY_PATH
       * variable.
       */
      runpath = getenv("LD_LIBRARY_PATH");
#endif /* __sun */

#ifdef __linux
      /*
       * On linux, if a binary is running as sgid or suid, glibc sets
       * LD_LIBRARY_PATH to the empty string for security purposes.  (In
       * contrast, on Solaris the LD_LIBRARY_PATH variable for a
       * privileged binary does not lose its settings; but the dynamic
       * linker does apply more scrutiny to the path.) The launcher uses
       * the value of LD_LIBRARY_PATH to prevent an exec loop.
       * Therefore, if we are running sgid or suid, this function's
       * setting of LD_LIBRARY_PATH will be ineffective and we should
       * return from the function now.  Getting the right libraries to
       * be found must be handled through other mechanisms.
       */
      if((getgid() != getegid()) || (getuid() != geteuid()) ) {
	return;
      }
#endif    

      /* runpath contains current effective LD_LIBRARY_PATH setting */

      jvmpath = strdup(jvmpath);
      new_runpath = MemAlloc( ((runpath!=NULL)?strlen(runpath):0) + 
			      2*strlen(jrepath) + 2*strlen(arch) +
			      strlen(jvmpath) + 52);
      newpath = new_runpath + strlen("LD_LIBRARY_PATH=");


      /*
       * Create desired LD_LIBRARY_PATH value for target data model.
       */
      {
	/* remove the name of the .so from the JVM path */
	lastslash = strrchr(jvmpath, '/');
	if (lastslash)
	  *lastslash = '\0';


	/* jvmpath, ((running != wanted)?((wanted==64)?"/"BIG_ARCH:"/.."):""), */

	sprintf(new_runpath, "LD_LIBRARY_PATH="
		"%s:"
		"%s/lib/%s:"
		"%s/../lib/%s",
		jvmpath,
#ifdef DUAL_MODE
		jrepath, ((wanted==64)?BIG_ARCH:SMALL_ARCH),
		jrepath, ((wanted==64)?BIG_ARCH:SMALL_ARCH)
#else
		jrepath, arch,
		jrepath, arch
#endif
		);


	/* 
	 * Check to make sure that the prefix of the current path is the 
	 * desired environment variable setting.
	 */
	if (runpath != NULL && 
	    strncmp(newpath, runpath, strlen(newpath))==0 &&
	    (runpath[strlen(newpath)] == 0 || runpath[strlen(newpath)] == ':') &&
	    (running == wanted) /* data model does not have to be changed */
#ifdef __sun
	    && (dmpath == NULL)    /* data model specific variables not set  */
#endif
	    ) {

	  return; /* already have right LD_LIBRARY_PATH (and data model,
		     where appropriate)*/
	}
      }

      /* 
       * Place the desired environment setting onto the prefix of
       * LD_LIBRARY_PATH.  Note that this prevents any possible infinite
       * loop of execv() because we test for the prefix, above.
       */
      if (runpath != 0) {
	strcat(new_runpath, ":");
	strcat(new_runpath, runpath);
      }
    
      if( putenv(new_runpath) != 0) {
	exit(1); /* problem allocating memory; LD_LIBRARY_PATH not set
		    properly */
      }

      /* 
       * Unix systems document that they look at LD_LIBRARY_PATH only
       * once at startup, so we have to re-exec the current executable
       * to get the changed environment variable to have an effect.
       */

#ifdef __sun
      /*
       * If dmpath is not NULL, remove the data model specific string
       * in the environment for the exec'ed child.
       */

      if( dmpath != NULL)
	(void)UnsetEnv((wanted==32)?"LD_LIBRARY_PATH_32":"LD_LIBRARY_PATH_64");
#endif

      newenvp = environ;

      {
	char *newexec = execname;
#ifdef DUAL_MODE
	/* 
	 * If the data model is being changed, the path to the
	 * executable must be updated accordingly; the executable name
	 * and directory the executable resides in are separate.  In the
	 * case of 32 => 64, the new bits are assumed to reside in, e.g.
	 * "olddir/BIGARCH/execname"; in the case of 64 => 32,
	 * the bits are assumed to be in "olddir/../execname".  For example,
	 *
	 * olddir/sparcv9/execname
	 * olddir/amd64/execname
	 *
	 * for Solaris SPARC and amd64, respectively.
	 */

	if (running != wanted) {
	  char *oldexec = strcpy(MemAlloc(strlen(execname) + 1), execname);
	  char *olddir = oldexec;
	  char *oldbase = strrchr(oldexec, '/');

	
	  newexec = MemAlloc(strlen(execname) + 20);
	  *oldbase++ = 0;
	  sprintf(newexec, "%s/%s/%s", olddir, 
		  ((wanted==64) ? BIG_ARCH : ".."), oldbase);
	  argv[0] = newexec;
	} 
#endif

	execve(newexec, argv, newenvp);
	perror("execv()");

	fprintf(stderr, "Error trying to exec %s.\n", newexec);
	fprintf(stderr, "Check if file exists and permissions are set correctly.\n");

#ifdef DUAL_MODE
	if (running != wanted) {
	  fprintf(stderr, "Failed to start a %d-bit JVM process from a %d-bit JVM.\n",
		  wanted, running);
#  ifdef __sun

#    ifdef __sparc
	  fprintf(stderr, "Verify all necessary J2SE components have been installed.\n" );
	  fprintf(stderr,
		  "(Solaris SPARC 64-bit components must be installed after 32-bit components.)\n" );
#    else 
	  fprintf(stderr, "Either 64-bit processes are not supported by this platform\n");
	  fprintf(stderr, "or the 64-bit components have not been installed.\n");
#    endif
	}
#  endif
#endif

      }

      exit(1);
    }

}


/*
 * On Solaris VM choosing is done by the launcher (java.c).
 */
static jboolean
GetJVMPath(const char *jrepath, const char *jvmtype,
	   char *jvmpath, jint jvmpathsize, char * arch)
{
    struct stat s;
    
    if (strchr(jvmtype, '/')) {
	sprintf(jvmpath, "%s/" JVM_DLL, jvmtype);
    } else {
	sprintf(jvmpath, "%s/lib/%s/%s/" JVM_DLL, jrepath, arch, jvmtype);
    }
    if (_launcher_debug)
      printf("Does `%s' exist ... ", jvmpath);

    if (stat(jvmpath, &s) == 0) {
	if (_launcher_debug) 
	  printf("yes.\n");
	return JNI_TRUE;
    } else {
	if (_launcher_debug)
	  printf("no.\n");
	return JNI_FALSE;
    }
}

/*
 * Find path to JRE based on .exe's location or registry settings.
 */
static jboolean
GetJREPath(char *path, jint pathsize, char * arch, jboolean speculative)
{
    char libjava[MAXPATHLEN];

    if (GetApplicationHome(path, pathsize)) {
	/* Is JRE co-located with the application? */
	sprintf(libjava, "%s/lib/%s/" JAVA_DLL, path, arch);
	if (access(libjava, F_OK) == 0) {
	    goto found;
	}

	/* Does the app ship a private JRE in <apphome>/jre directory? */
	sprintf(libjava, "%s/jre/lib/%s/" JAVA_DLL, path, arch);
	if (access(libjava, F_OK) == 0) {
	    strcat(path, "/jre");
	    goto found;
	}
    }

    if (!speculative) 
      fprintf(stderr, "Error: could not find " JAVA_DLL "\n");
    return JNI_FALSE;

 found:
    if (_launcher_debug)
      printf("JRE path is %s\n", path);
    return JNI_TRUE;
}

jboolean
LoadJavaVM(const char *jvmpath, InvocationFunctions *ifn)
{
    Dl_info dlinfo;
    void *libjvm;

    if (_launcher_debug) {
	printf("JVM path is %s\n", jvmpath);
    }

    libjvm = dlopen(jvmpath, RTLD_NOW + RTLD_GLOBAL);
    if (libjvm == NULL) {
#if defined(__sparc) && !defined(_LP64) /* i.e. 32-bit sparc */
      FILE * fp;
      Elf32_Ehdr elf_head;
      int count;
      int location;
      
      fp = fopen(jvmpath, "r");
      if(fp == NULL)
	goto error;
    
      /* read in elf header */
      count = fread((void*)(&elf_head), sizeof(Elf32_Ehdr), 1, fp);
      fclose(fp);
      if(count < 1)
	goto error;

      /* 
       * Check for running a server vm (compiled with -xarch=v8plus)
       * on a stock v8 processor.  In this case, the machine type in
       * the elf header would not be included the architecture list
       * provided by the isalist command, which is turn is gotten from
       * sysinfo.  This case cannot occur on 64-bit hardware and thus
       * does not have to be checked for in binaries with an LP64 data
       * model.
       */
      if(elf_head.e_machine == EM_SPARC32PLUS) {
	char buf[257];  /* recommended buffer size from sysinfo man
			   page */
	long length;
	char* location;
	
	length = sysinfo(SI_ISALIST, buf, 257);
	if(length > 0) {
	  location = strstr(buf, "sparcv8plus ");
	  if(location == NULL) {
	    fprintf(stderr, "SPARC V8 processor detected; Server compiler requires V9 or better.\n");
	    fprintf(stderr, "Use Client compiler on V8 processors.\n");
	    fprintf(stderr, "Could not create the Java virtual machine.\n");
	    return JNI_FALSE;
	  }
	}
      }
#endif 
      fprintf(stderr, "dl failure on line %d", __LINE__);
      goto error;
    }

    ifn->CreateJavaVM = (CreateJavaVM_t)
      dlsym(libjvm, "JNI_CreateJavaVM");
    if (ifn->CreateJavaVM == NULL)
	goto error;

    ifn->GetDefaultJavaVMInitArgs = (GetDefaultJavaVMInitArgs_t)
	dlsym(libjvm, "JNI_GetDefaultJavaVMInitArgs");
    if (ifn->GetDefaultJavaVMInitArgs == NULL)
      goto error;

    return JNI_TRUE;

error:
    fprintf(stderr, "Error: failed %s, because %s\n", jvmpath, dlerror());
    return JNI_FALSE;
}

/*
 * Get the path to the file that has the usage message for -X options.
 */
void
GetXUsagePath(char *buf, jint bufsize)
{
    Dl_info dlinfo;
   
    /* we use RTLD_NOW because of problems with ld.so.1 and green threads */
    dladdr(dlsym(dlopen(JVM_DLL, RTLD_NOW), "JNI_CreateJavaVM"), &dlinfo);
#ifdef __linux__
    strncpy(buf, (char *)dlinfo.dli_fname, bufsize - 2);
#else
    strncpy(buf, (char *)dlinfo.dli_fname, bufsize - 1);
#endif

    buf[bufsize-1] = '\0';
    *(strrchr(buf, '/')) = '\0';
    strcat(buf, "/Xusage.txt");
}

/*
 * If app is "/foo/bin/javac", or "/foo/bin/sparcv9/javac" then put
 * "/foo" into buf.
 */
jboolean
GetApplicationHome(char *buf, jint bufsize)
{
#ifdef __linux__
    char *execname = GetExecname();
    if (execname) {
	strncpy(buf, execname, bufsize-1);
	buf[bufsize-1] = '\0';
    } else {
	return JNI_FALSE;
    }
#else
    Dl_info dlinfo;

    dladdr((void *)GetApplicationHome, &dlinfo);
    if (realpath(dlinfo.dli_fname, buf) == NULL) {
	fprintf(stderr, "Error: realpath(`%s') failed.\n", buf);
	return JNI_FALSE;
    }
#endif

    if (strrchr(buf, '/') == 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    *(strrchr(buf, '/')) = '\0';	/* executable file      */
    if (strlen(buf) < 4 || strrchr(buf, '/') == 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    if (strcmp("/bin", buf + strlen(buf) - 4) != 0) 
	*(strrchr(buf, '/')) = '\0';	/* sparcv9 or amd64     */
    if (strlen(buf) < 4 || strcmp("/bin", buf + strlen(buf) - 4) != 0) {
	buf[0] = '\0';
	return JNI_FALSE;
    }
    *(strrchr(buf, '/')) = '\0';	/* bin                  */

    return JNI_TRUE;
}


/*
 * Return true if the named program exists
 */
static int
ProgramExists(char *name)
{
    struct stat sb;
    if (stat(name, &sb) != 0) return 0;
    if (S_ISDIR(sb.st_mode)) return 0;
    return (sb.st_mode & S_IEXEC) != 0;
}


/*
 * Find a command in a directory, returning the path.
 */
static char *
Resolve(char *indir, char *cmd)
{
    char name[PATH_MAX + 2], *real;

    if ((strlen(indir) + strlen(cmd) + 1)  > PATH_MAX) return 0;
    sprintf(name, "%s%c%s", indir, FILE_SEPARATOR, cmd);
    if (!ProgramExists(name)) return 0;
    real = MemAlloc(PATH_MAX + 2);
    if (!realpath(name, real)) 
	strcpy(real, name);
    return real;
}


/*
 * Find a path for the executable
 */
static char *
FindExecName(char *program)
{
    char cwdbuf[PATH_MAX+2];
    char *path;
    char *tmp_path;
    char *f;
    char *result = NULL;

    /* absolute path? */
    if (*program == FILE_SEPARATOR || 
	(FILE_SEPARATOR=='\\' && strrchr(program, ':')))
	return Resolve("", program+1);

    /* relative path? */
    if (strrchr(program, FILE_SEPARATOR) != 0) {
	char buf[PATH_MAX+2];
	return Resolve(getcwd(cwdbuf, sizeof(cwdbuf)), program);
    }

    /* from search path? */
    path = getenv("PATH");
    if (!path || !*path) path = ".";
    tmp_path = MemAlloc(strlen(path) + 2);
    strcpy(tmp_path, path);

    for (f=tmp_path; *f && result==0; ) {
	char *s = f;
	while (*f && (*f != PATH_SEPARATOR)) ++f;
	if (*f) *f++ = 0;
	if (*s == FILE_SEPARATOR)
	    result = Resolve(s, program);
	else {
	    /* relative path element */
	    char dir[2*PATH_MAX];
	    sprintf(dir, "%s%c%s", getcwd(cwdbuf, sizeof(cwdbuf)), 
		    FILE_SEPARATOR, s);
	    result = Resolve(dir, program);
	}
	if (result != 0) break;
    }

    free(tmp_path);
    return result;
}


/* Store the name of the executable once computed */
static char *execname = NULL;

/*
 * Compute the name of the executable
 *
 * In order to re-exec securely we need the absolute path of the
 * executable. On Solaris getexecname(3c) may not return an absolute
 * path so we use dladdr to get the filename of the executable and
 * then use realpath to derive an absolute path. From Solaris 9
 * onwards the filename returned in DL_info structure from dladdr is
 * an absolute pathname so technically realpath isn't required.
 * On Linux we read the executable name from /proc/self/exe.
 * As a fallback, and for platforms other than Solaris and Linux,
 * we use FindExecName to compute the executable name.
 */
static char *
SetExecname(char **argv)
{
    char* exec_path = NULL;

    if (execname != NULL)	/* Already determined */
	return (execname);
   
#if defined(__sun)
    {
        Dl_info dlinfo;
        if (dladdr((void*)&SetExecname, &dlinfo)) {
	    char *resolved = (char*)MemAlloc(PATH_MAX+1);
   	    if (resolved != NULL) {
		exec_path = realpath(dlinfo.dli_fname, resolved);
		if (exec_path == NULL) {
		    free(resolved);
		}
	    }
        }
    }
#elif defined(__linux__)
    {
	const char* self = "/proc/self/exe";
        char buf[PATH_MAX+1];
        int len = readlink(self, buf, PATH_MAX);
        if (len >= 0) {
	    buf[len] = '\0';		/* readlink doesn't nul terminate */
	    exec_path = strdup(buf);
	}
    }
#else /* !__sun && !__linux */
    {
        /* Not implemented */
    }
#endif 

    if (exec_path == NULL) {
        exec_path = FindExecName(argv[0]);
    }
    execname = exec_path;
    return exec_path;
}

/*
 * Return the name of the executable.  Used in java_md.c to find the JRE area.
 */
static char *
GetExecname() {
  return execname;
}

void ReportErrorMessage(char * message, jboolean always) {
  if (always) {
    fprintf(stderr, "%s\n", message);
  }
}

void ReportErrorMessage2(char * format, char * string, jboolean always) {
  if (always) {
    fprintf(stderr, format, string);
    fprintf(stderr, "\n");
  }
}

void  ReportExceptionDescription(JNIEnv * env) {
  (*env)->ExceptionDescribe(env);
}

/*
 * Return JNI_TRUE for an option string that has no effect but should
 * _not_ be passed on to the vm; return JNI_FALSE otherwise.  On
 * Solaris, this screening needs to be done if:
 * 1) LD_LIBRARY_PATH does _not_ need to be reset and
 * 2) -d32 or -d64 is passed to a binary with a matching data model
 *    (the exec in SetLibraryPath removes -d<n> options and points the
 *    exec to the proper binary).  When this exec is not done, these options
 *    would end up getting passed onto the vm.
 */
jboolean RemovableMachineDependentOption(char * option) {
#ifdef __sun
  /*
   * Unconditionally remove both -d32 and -d64 options since only
   * the last such options has an effect; e.g. 
   * java -d32 -d64 -d32 -version
   * is equivalent to 
   * java -d32 -version
   */

  if( (strcmp(option, "-d32")  == 0 ) || 
      (strcmp(option, "-d64")  == 0 ))
    return JNI_TRUE;
  else
    return JNI_FALSE;
#else /* not __sun */
  return JNI_FALSE;
#endif
}

void PrintMachineDependentOptions() {
#ifdef __sun
      fprintf(stdout,
	"    -d32          use a 32-bit data model if available\n"
	"\n"
	"    -d64          use a 64-bit data model if available\n");
#endif
      return;
}

jboolean
ServerClassMachine() {
  /* Stub for 1.5 backport */
  jboolean result = JNI_FALSE;
  return result;
}

/*
 *	Determine if the candidate directory contains an appropriate JVM.
 *
 *	To accommodate Solaris patches, the full release/version name
 *	can't always be determined from the version portion of the
 *	directory name.  In these cases a file "RELEASE" should be
 *	present in the directory and used to identify the full release
 *	name.
 *
 *	Since using the file system as a registry is a bit risky, perform
 *	additional sanity checks on the identified directory to validate
 *	it as a valid jre/sdk.
 *
 *	Returns true (non-zero) if the directory is an appropriate
 *	candidate based on passing the validity tests and the identified
 *	release; otherwise returns false (zero).
 *
 *	Note that checking for anything more than the existence of an
 *	executable object at bin/java relative to the path being checked
 *	will break the regression tests.
 *
 *	Parameters:
 *	    release:	A buffer of a least MAXNAMELEN + 1 characters in
 *			which the release name is returned upon success.
 *	    path:	The prefix path to the directory being considered.
 *	    dir:	The directory being considered.
 *	    version:	The version portion of the directory name being
 *			considered.
 *	    spec:	The requested release specification.
 */
static int
AppropriateJVM(char *release, const char *path, const char *dir,
  const char *version, const char *spec)
{
    char    *javahome;
    int     fd;
    int     len;
    char    buffer[PATH_MAX];

    if (strlen(path) + strlen(dir) + 11 > PATH_MAX)
	return (0);	/* Silently reject "impossibly" long paths */

    /*
     * Construct the constant portion of the path and maintain a pointer
     * to the end of the constant portion (the equivalent of $JAVAHOME).
     */
    (void)strcat(strcat(strcpy(buffer, path), "/"), dir);
    javahome = &buffer[strlen(buffer)];

    /*
     * See if there is a possible Java Virtual Machine at the appropriate
     * relative location from $JAVAHOME.  If not, bail.
     */
    (void)strcpy(javahome, "/bin/java");
    if (access(buffer, X_OK) != 0)
	return (0);

    /*
     * See if there is a file named "RELEASE" in the potential $JAVAHOME
     * directory. If so, assume it contains the full release information.
     */
    (void)strcpy(javahome, "/RELEASE");
    if ((fd = open(buffer, O_RDONLY)) != -1) {
	if ((len = read(fd, release, MAXNAMELEN)) <= 0)
	    return (0);				/* corrupt or zero length */
	(void)close(fd);
	release[len] = '\0';
	len = strcspn(release," \t\n\r\f");	/* terminate at cntl char */
	release[len] = '\0';
	return (acceptable_release(release, spec));
    }

    /*
     * Determine if the version part of the directory name is an acceptable
     * release.
     */
    if (acceptable_release(version, spec)) {
	(void)strcpy(release, version);
	return (1);
    }
    return (0);
}

/*
 *	Determine if there is an acceptable JRE in the directory dirname.
 *	Upon locating the "best" one, return a fully qualified path to
 *	it. "Best" is defined as the most advanced JRE meeting the
 *	constraints contained in the manifest_info. If no JRE in this
 *	directory meets the constraints, return NULL.
 *
 *	Note that we don't check for errors in reading the directory
 *	(which would be done by checking errno).  This is because it
 *	doesn't matter if we get an error reading the directory, or
 *	we just don't find anything interesting in the directory.  We
 *	just return NULL in either case.
 *
 *	The historical names of j2sdk and j2re were changed to jdk and
 *	jre respecively as part of the 1.5 rebranding effort.  Since the
 *	former names are legacy on Linux, they must be recognized for
 *	all time.  Fortunately, this is a minor cost.
 */
static char
*ProcessDir(manifest_info *info, char *dirname)
{
    DIR	    *dirp;
    struct dirent *dp;
    int     offset;			/* offset to version in the dir name */
    char    version[MAXNAMELEN + 1];	/* extracted version string */
    char    *best = NULL;		/* "best" dir name */
    char    *best_version = NULL;	/* "best" version string */
    char    *ret_str = NULL;

    if ((dirp = opendir(dirname)) == NULL)
	return (NULL);

    do {
	if ((dp = readdir(dirp)) != NULL) {
	    if ((strncmp(dp->d_name, "jre", 3) == 0) ||
	        (strncmp(dp->d_name, "jdk", 3) == 0))
		offset = 3;
	    else if (strncmp(dp->d_name, "j2re", 4) == 0)
		offset = 4;
	    else if (strncmp(dp->d_name, "j2sdk", 5) == 0)
		offset = 5;
	    else
		continue;
	    if (AppropriateJVM(version, dirname, dp->d_name,
	      dp->d_name + offset, info->jre_version)) {
		if ((best == NULL) ||
		    (exact_version_id(version, best_version) > 0)) {
		    free(best);
		    best = strdup(dp->d_name);
		    free(best_version);
		    best_version = strdup(version);
		}
	    }
	}
    } while (dp != NULL);
    (void) closedir(dirp);
    free(best_version);

    if (best == NULL)
	return (NULL);
    else {
	ret_str = MemAlloc(strlen(dirname) + strlen(best) + 2);
	ret_str = strcat(strcat(strcpy(ret_str, dirname), "/"), best);
	free(best);
	return (ret_str);
    }
}

/*
 *	This is the global entry point. It examines the host for the optimal
 *	JRE to be used by scanning a set of directories.  The set of directories
 *	is platform dependent and can be overridden by the environment
 *	variable JAVA_VERSION_PATH.
 *
 *	This routine itself simply determines the set of appropriate
 *	directories before passing control onto ProcessDir().
 */
char*
LocateJRE(manifest_info* info)
{
    char	*path;
    char	*home;
    char	*target = NULL;
    char	*dp;
    char	*cp;

    /*
     * Start by getting JAVA_VERSION_PATH
     */
    if (info->jre_restrict_search)
	path = strdup(system_dir);
    else if ((path = getenv("JAVA_VERSION_PATH")) != NULL)
	path = strdup(path);
    else
	if ((home = getenv("HOME")) != NULL) {
	    path = (char *)MemAlloc(strlen(home) + 13);
	    path = strcat(strcat(strcat(strcpy(path, home),
	        user_dir), ":"), system_dir);
	} else
	    path = strdup(system_dir);

    /*
     * Step through each directory on the path. Terminate the scan with
     * the first directory with an acceptable JRE.
     */
    cp = dp = path;
    while (dp != NULL) {
	cp = strchr(dp, (int)':');
	if (cp != NULL)
	    *cp = (char)NULL;
	if ((target = ProcessDir(info, dp)) != NULL)
	    break;
	dp = cp;
	if (dp != NULL)
	    dp++;
    }
    free(path);
    return (target);
}

/*
 * Given a path to a jre to execute, this routine checks if this process
 * is indeed that jre.  If not, it exec's that jre.
 *
 * We want to actually check the paths rather than just the version string
 * built into the executable, so that given version specification (and
 * JAVA_VERSION_PATH) will yield the exact same Java environment, regardless
 * of the version of the arbitrary launcher we start with.
 */
void
ExecJRE(char *jre, char **argv)
{
    char    wanted[PATH_MAX];
    char    *execname;
    char    *progname;

    /*
     * Resolve the real path to the directory containing the selected JRE.
     */
    if (realpath(jre, wanted) == NULL) {
	fprintf(stderr, "Unable to resolve %s\n", jre);
	exit(1);
    }

    /*
     * Resolve the real path to the currently running launcher.
     */
    execname = SetExecname(argv);
    if (execname == NULL) {
	fprintf(stderr, "Unable to resolve current executable\n");
	exit(1);
    }

    /*
     * If the path to the selected JRE directory is a match to the initial
     * portion of the path to the currently executing JRE, we have a winner!
     * If so, just return.
     */
    if (strncmp(wanted, execname, strlen(wanted)) == 0)
	return;			/* I am the droid you were looking for */

    /*
     * If this isn't the selected version, exec the selected version.
     */
#ifdef JAVA_ARGS  /* javac, jar and friends. */
    progname = "java";
#else             /* java, oldjava, javaw and friends */
#ifdef PROGNAME
    progname = PROGNAME;
#else
    progname = *argv;
    if ((s = strrchr(progname, FILE_SEPARATOR)) != 0) {
        progname = s + 1;
    }
#endif /* PROGNAME */
#endif /* JAVA_ARGS */

    /*
     * This should never happen (because of the selection code in SelectJRE),
     * but check for "impossibly" long path names just because buffer overruns
     * can be so deadly.
     */
    if (strlen(wanted) + strlen(progname) + 6 > PATH_MAX) {
	fprintf(stderr, "Path length exceeds maximum length (PATH_MAX)\n");
	exit(1);
    }

    /*
     * Construct the path and exec it.
     */
    (void)strcat(strcat(wanted, "/bin/"), progname);
    argv[0] = progname;
    if (_launcher_debug) {
	int i;
	printf("execv(\"%s\"", wanted);
	for (i = 0; argv[i] != NULL; i++)
	    printf(", \"%s\"", argv[i]);
	printf(")\n");
    }
    execv(wanted, argv);
    fprintf(stderr, "Exec of %s failed\n", wanted);
    exit(1);
}

/*
 * "Borrowed" from Solaris 10 where the unsetenv() function is being added
 * to libc thanks to SUSv3 (Standard Unix Specification, version 3). As
 * such, in the fullness of time this will appear in libc on all relevant
 * Solaris/Linux platforms and maybe even the Windows platform.  At that
 * time, this stub can be removed.
 *
 * This implementation removes the environment locking for multithreaded
 * applications.  (We don't have access to these mutexes within libc and
 * the launcher isn't multithreaded.)  Note that what remains is platform
 * independent, because it only relies on attributes that a POSIX environment
 * defines.
 *
 * Returns 0 on success, -1 on failure.
 *
 * Also removed was the setting of errno.  The only value of errno set
 * was EINVAL ("Invalid Argument").
 */

/*
 * s1(environ) is name=value
 * s2(name) is name(not the form of name=value).
 * if names match, return value of 1, else return 0
 */
static int
match_noeq(const char *s1, const char *s2)
{
	while (*s1 == *s2++) {
		if (*s1++ == '=')
			return (1);
	}
	if (*s1 == '=' && s2[-1] == '\0')
		return (1);
	return (0);
}

/*
 * added for SUSv3 standard
 *
 * Delete entry from environ.
 * Do not free() memory!  Other threads may be using it.
 * Keep it around forever.
 */
static int
borrowed_unsetenv(const char *name)
{
	long	idx;		/* index into environ */

	if (name == NULL || *name == '\0' ||
	    strchr(name, '=') != NULL) {
		return (-1);
	}

	for (idx = 0; environ[idx] != NULL; idx++) {
		if (match_noeq(environ[idx], name))
			break;
	}
	if (environ[idx] == NULL) {
		/* name not found but still a success */
		return (0);
	}
	/* squeeze up one entry */
	do {
		environ[idx] = environ[idx+1];
	} while (environ[++idx] != NULL);

	return (0);
}
/* --- End of "borrowed" code --- */

/*
 * Wrapper for unsetenv() function.
 */
int
UnsetEnv(char *name)
{
    return(borrowed_unsetenv(name));
}
