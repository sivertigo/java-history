/*
 * @(#)java.c	1.33 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

/*
 * Shared source for 'java' command line tool.
 *
 * If JAVA_ARGS is defined, then acts as a launcher for applications. For
 * instance, the JDK command line tools such as javac and javadoc (see
 * makefiles for more details) are built with this program.  Any arguments
 * prefixed with '-J' will be passed directly to the 'java' command.
 *
 * If OLDJAVA is defined then enables old-style launcher behavior. In the
 * old launcher, both application and system classes are loaded from the
 * system class path.  In the new launcher, there is a separate class path
 * and class loader for loading application classes.
 */

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "jvm.h"	/* for JVM_PrintXUsage() */
#include "java.h"

#ifndef FULL_VERSION
#define FULL_VERSION "1.2"
#endif

static jboolean printVersion = JNI_FALSE;

static char *progname;
jboolean debug = JNI_FALSE;

/*
 * List of VM options to be specified when the VM is created.
 */
static JavaVMOption *options;
static int numOptions, maxOptions;

/*
 * Prototypes for functions internal to launcher.
 */
static void AddOption(char *str, void *info);
static void SetClassPath(char *s);
static jboolean ParseArguments(int *pargc, char ***pargv, char **pjarfile,
			       char **pclassname, int *pret);
static jboolean InitializeJVM(JavaVM **pvm, JNIEnv **penv,
				 InvocationFunctions *ifn);
static void* MemAlloc(size_t size);
static jstring NewPlatformString(JNIEnv *env, char *s);
static jobjectArray NewPlatformStringArray(JNIEnv *env, char **strv, int strc);
static jclass LoadClass(JNIEnv *env, char *name);
static jstring GetMainClassName(JNIEnv *env, char *jarname);

#ifdef JAVA_ARGS
static void TranslateDashJArgs(int *pargc, char ***pargv);
static jboolean AddApplicationOptions(void);
#endif

static void PrintJavaVersion(JNIEnv *env);
static void PrintUsage(void);
static jint PrintXUsage(void);

/*
 * Entry point.
 */
int
main(int argc, char **argv)
{
    JavaVM *vm = 0;
    JNIEnv *env = 0;
    char *jarfile = 0;
    char *classname = 0;
    char *s = 0;
    jclass mainClass;
    jmethodID mainID;
    jobjectArray mainArgs;
    int ret;
    InvocationFunctions ifn;
    char *jvmtype = "exact";
    jboolean jvmspecified = JNI_FALSE;     /* Assume no option specified. */
    jlong start, end;

    if (getenv("_JAVA_LAUNCHER_DEBUG") != 0) {
	debug = JNI_TRUE;
	printf("----_JAVA_LAUNCHER_DEBUG----\n");
    }

    /*
     * Did the user pass a -classic or -hotspot as the first option to
     * the launcher?
     */
    if (argc > 1) {
	if (strcmp(argv[1], "-hotspot") == 0) {
	    jvmtype = "hotspot";
	    jvmspecified = JNI_TRUE;
	} else if (strcmp(argv[1], "-classic") == 0) {
#	    ifdef JS_ONLY
	    jvmtype = "classic";
	    jvmspecified = JNI_TRUE;
#	    endif
	}
    }

    ifn.CreateJavaVM = 0;
    ifn.GetDefaultJavaVMInitArgs = 0;
    if (!LoadJavaVM(jvmtype, &ifn))
	return 1;

    /* Grab the program name */
    progname = *argv++;
    if ((s = strrchr(progname, FILE_SEPARATOR)) != 0) {
	progname = s + 1;
    }
    --argc;

    /* Skip over a specified -classic/-hotspot option */
    if (jvmspecified) {
	argv++;
	argc--;
    }

#ifdef JAVA_ARGS
    /* Preprocess wrapper arguments */
    TranslateDashJArgs(&argc, &argv);
    if (!AddApplicationOptions())
	return 1;
#endif

    /* Set default CLASSPATH */
    /*
     * XXX: Need to reconcile this code with JavaSoft's latest (making sure
     *	    that embedded apps written to the 1.1 JNI get a proper application
     *	    classpath).
     * XXX: Could probably pull the conditional compilation on JAVA_ARGS out
     *	    to wrap this entire calculation.
     */
#   ifndef OLDJAVA
    {
	if ((s = getenv("CLASSPATH")) == 0) {
	    s = ".";
	}
#	ifndef JAVA_ARGS
	SetClassPath(s);
#	endif
    }
#   else
    {
	/*
	 * Append the CLASSPATH to the default system class path (done in
	 * JNI_GetDefaultJavaVMInitArgs in our version)
	 */
	JDK1_1InitArgs args;
	args.version = JNI_VERSION_1_1;
	if (JNI_GetDefaultJavaVMInitArgs(&args) != JNI_OK) {
	    fprintf(stderr, "Could not get default VM args\n");
	    return 1;
	}
#	ifndef JAVA_ARGS
	SetClassPath(args.classpath);
#	endif
    }
#   endif

    /* Parse command line options */
    if (!ParseArguments(&argc, &argv, &jarfile, &classname, &ret)) {
	return ret;
    }

    /* Override class path if -jar flag was specified */
    if (jarfile != 0) {
	SetClassPath(jarfile);
    }

    /* Initialize the virtual machine */

    if (debug)
	start = CounterGet();

    if (!InitializeJVM(&vm, &env, &ifn)) {
	fprintf(stderr, "Could not create the Java virtual machine.\n");
	/*
	 * XXX:	The previous version checked to see whether the JVM was
	 *	initialized enough that a more precise error could be emiited
	 *	than the fprintf above.  Investigate restoring that code.  (It
	 *	would have to be moved within this if statement.)
	 */
	return 1;
    }

    if (printVersion) {
        PrintJavaVersion(env);
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    ret = 1;
	} else {
	    ret = 0;
	}
	goto leave;
    }

    /* If the user specified neither a class name or a JAR file */
    if (jarfile == 0 && classname == 0) {
	PrintUsage();
	goto leave;
    }

    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to InitializeJVM\n",
	       (jint)Counter2Micros(end-start));
    }

    /* At this stage, argc/argv have the applications' arguments */
    if (debug) {
	int i = 0;
	printf("Main-Class is '%s'\n", classname ? classname : "");
	printf("Apps' argc is %d\n", argc);
	for (; i < argc; i++) {
	    printf("    argv[%2d] = '%s'\n", i, argv[i]);
	}
    }

    /*
     * XXX: Handle -Xdescribe here?  Benefit:  Wouldn't have to accompany it
     *	    with a class needed for no other purpose.  Drawback:  Promotes
     *	    JVM-specific code to this ostensibly JVM-independent file.
     */

    ret = 1;

    /*
     * XXX: This code reports errors with (*env)->ExceptionDescribe() in place
     *	    of the former JVM_PrintException().  ExceptionDescribe seems to
     *	    emit considerably less information than JVM_PrintException.
     *	    Should we restore JVM_PrintException here?  See also the comment
     *	    below for main method invocation failure, which seems to
     *	    contradict the "less info" claim made here (since
     *	    JVM_PrintException calls the uncaughtException method).
     */
    /* Get the application's main class */
    if (jarfile != 0) {
	jstring mainClassName = GetMainClassName(env, jarfile);
	if (mainClassName == NULL) {
	    fprintf(stderr, "Failed to load Main-Class manifest attribute "
		    "from\n%s\n", jarfile);
	    goto leave;
	}
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	    goto leave;
	}
	classname = (char *)(*env)->GetStringUTFChars(env, mainClassName, 0);
	if (classname == NULL) {
	    (*env)->ExceptionDescribe(env);
	    goto leave;
	}
	mainClass = LoadClass(env, classname);
	(*env)->ReleaseStringUTFChars(env, mainClassName, classname);
    } else {
	mainClass = LoadClass(env, classname);
    }
    if (mainClass == NULL) {
        (*env)->ExceptionDescribe(env);
	goto leave;
    }

    /* Get the application's main method */
    mainID = (*env)->GetStaticMethodID(env, mainClass, "main",
				       "([Ljava/lang/String;)V");
    if (mainID == NULL) {
	if ((*env)->ExceptionOccurred(env)) {
	    (*env)->ExceptionDescribe(env);
	} else {
	    fprintf(stderr, "No main method found in specified class.\n");
	}
	goto leave;
    }

    /* Build argument array */
    mainArgs = NewPlatformStringArray(env, argv, argc);
    if (mainArgs == NULL) {
	(*env)->ExceptionDescribe(env);
	goto leave;
    }

    /* Invoke main method. */
    (*env)->CallStaticVoidMethod(env, mainClass, mainID, mainArgs);
    if ((*env)->ExceptionOccurred(env)) {
	/* Formerly, we used to call the "uncaughtException" method of the
	   main thread group, but this was later shown to be unnecessary
	   since the default definition merely printed out the same exception
	   stack trace as ExceptionDescribe and could never actually be
	   overridden by application programs. */
	(*env)->ExceptionDescribe(env);
	goto leave;
    }

    /*
     * Detach the current thread so that it appears to have exited when
     * the application's main method exits.
     */
    if ((*vm)->DetachCurrentThread(vm) != JNI_OK) {
	fprintf(stderr, "Could not detach main thread.\n");
	goto leave;
    }
    ret = 0;

leave:
    (void) (*vm)->DestroyJavaVM(vm);
    return ret;
}

/*
 * Adds a new VM option with the given given name and value.
 */
static void
AddOption(char *str, void *info)
{
    /*
     * Expand options array if needed to accomodate at least one more
     * VM option.
     */
    if (numOptions >= maxOptions) {
	if (options == 0) {
	    maxOptions = 4;
	    options = MemAlloc(maxOptions * sizeof(JavaVMOption));
	} else {
	    JavaVMOption *tmp;
	    maxOptions *= 2;
	    tmp = MemAlloc(maxOptions * sizeof(JavaVMOption));
	    memcpy(tmp, options, numOptions * sizeof(JavaVMOption));
	    free(options);
	    options = tmp;
	}
    }
    options[numOptions].optionString = str;
    options[numOptions++].extraInfo = info;
}

static void
SetClassPath(char *s)
{
    char *def = MemAlloc(strlen(s) + 40);
#   ifdef OLDJAVA
    sprintf(def, "-Xbootclasspath:%s", s);
#   else
    sprintf(def, "-Djava.class.path=%s", s);
#   endif
    AddOption(def, NULL);
}

/*
 * Parses command line arguments.
 */
static jboolean
ParseArguments(int *pargc, char ***pargv, char **pjarfile, char **pclassname,
	int *pret)
{
    int argc = *pargc;
    char **argv = *pargv;
    jboolean jarflag = JNI_FALSE;
    char *arg;

    *pret = 1;
    while ((arg = *argv) != 0 && *arg == '-') {
	argv++; --argc;
	if (strcmp(arg, "-classpath") == 0 || strcmp(arg, "-cp") == 0) {
	    if (argc < 1) {
		fprintf(stderr, "%s requires class path specification\n", arg);
		PrintUsage();
		return JNI_FALSE;
	    }
	    SetClassPath(*argv);
	    argv++; --argc;
#	ifndef OLDJAVA
	} else if (strcmp(arg, "-jar") == 0) {
	    jarflag = JNI_TRUE;
#	endif
	} else if (strcmp(arg, "-help") == 0 || 
		   strcmp(arg, "-h") == 0 ||
		   strcmp(arg, "-?") == 0) {
	    PrintUsage();
	    *pret = 0;
	    return JNI_FALSE;
	} else if (strcmp(arg, "-version") == 0) {
	    printVersion = JNI_TRUE;
	    return JNI_TRUE;
	} else if (strcmp(arg, "-X") == 0) {
	    *pret = PrintXUsage();
	    return JNI_FALSE;

/*
 * The following cases provide backward compatibility with old-style
 * command line options.
 */ 
	} else if (strcmp(arg, "-fullversion") == 0) {
	    fprintf(stderr, "%s full version \"%s\"\n", progname, 
			FULL_VERSION);
	    *pret = 0;
	    return JNI_FALSE;
	} else if (strcmp(arg, "-verbosegc") == 0) {
	    AddOption("-verbose:gc", NULL);
	} else if (strcmp(arg, "-t") == 0) {
	    AddOption("-Xt", NULL);
	} else if (strcmp(arg, "-tm") == 0) {
	    AddOption("-Xtm", NULL);
	} else if (strcmp(arg, "-debug") == 0) {
	    AddOption("-Xdebug", NULL);
	} else if (strcmp(arg, "-noclassgc") == 0) {
	    AddOption("-Xnoclassgc", NULL);
	} else if (strcmp(arg, "-Xfuture") == 0) {
	    AddOption("-Xverify:all", NULL);
	} else if (strcmp(arg, "-verify") == 0) {
	    AddOption("-Xverify:all", NULL);
	} else if (strcmp(arg, "-verifyremote") == 0) {
	    AddOption("-Xverify:remote", NULL);
	} else if (strcmp(arg, "-noverify") == 0) {
	    AddOption("-Xverify:none", NULL);
	} else if (strncmp(arg, "-prof", 5) == 0) {
	    char *p = arg + 5;
	    char *tmp = MemAlloc(strlen(arg) + 50);
	    if (*p) {
	        sprintf(tmp, "-Xrunhprof:cpu=old,file=%s", p + 1);
	    } else {
	        sprintf(tmp, "-Xrunhprof:cpu=old,file=java.prof");
	    }
	    AddOption(tmp, NULL);

#	if 0	/* JS no longer accepts -jcov as compat option */
	/* XXX: Move down into the JVM-specific compat section? */
#define OPT_JCOV      "-prof=jcov"
#define OPT_JCOV_LEN  strlen(OPT_JCOV)
	} else if (strcmp(arg, OPT_JCOV) == 0) {
	    AddOption(arg, NULL);
	} else if ((strncmp(arg, OPT_JCOV ":", OPT_JCOV_LEN+1) == 0) && 
		   (strlen(arg) > OPT_JCOV_LEN + 1)) {
	    AddOption(arg, NULL);
#	endif
#	if 0	/* JS no longer accepts -l as compat option */
	/* XXX: Move down into the JVM-specific compat section? */
	} else if (strncmp(arg, "-l", 2) == 0 &&
		 (T = atoi(&arg[2])) >= 0) {
	    AddOption(arg, NULL);
#	endif
	/* Compatibility options that take arguments. */
#	define eqn(a, s)	strncmp(a, s, sizeof s - 1) == 0
	} else if (eqn(arg, "-ss")  ||
		   eqn(arg, "-oss") ||
		   eqn(arg, "-ms")  ||
		   eqn(arg, "-mx")) {
	    /* XXX:	Memory leak here... */
	    char *tmp = MemAlloc(strlen(arg) + 5);
	    sprintf(tmp, "-X%s", arg + 1); /* skip '-' */
	    AddOption(tmp, NULL);

	/*
	 * Compatibility options recognized only by this particular JVM;
	 * recognized in this form in addition to the -X form to avoid
	 * breaking the internal development environment (build scripts,
	 * tests, etc.).
	 */
	} else if (strcmp(arg, "-bcstats") == 0) {
	    AddOption("-Xbcstats", NULL);
	} else if (strcmp(arg, "-describe") == 0) {
	    AddOption("-Xdescribe", NULL);
	} else if (strcmp(arg, "-m") == 0) {
	    AddOption("-Xm", NULL);
	} else if (strcmp(arg, "-noagent") == 0) {
	} else if (strcmp(arg, "-stats") == 0) {
	} else if (strcmp(arg, "-verifyheap") == 0) {
	    AddOption("-Xverifyheap", NULL);
	/* Jvm-specific compatibility options that take arguments. */
	} else if (eqn(arg, "-debugport") ||
		   eqn(arg, "-maxjitcodesize") ||
		   eqn(arg, "-mr") ||
		   eqn(arg, "-my")) {
	    /* XXX:	Memory leak here... */
	    char *tmp = MemAlloc(strlen(arg) + 5);
	    sprintf(tmp, "-X%s", arg + 1); /* skip '-' */
	    AddOption(tmp, NULL);
#	undef eqn

	/*
	 * Options that are explicitly no longer supported.
	 */
	} else if (strcmp(arg, "-checksource") == 0 ||
		   strcmp(arg, "-cs") == 0 ||
		   strcmp(arg, "-noasyncgc") == 0) {
	    fprintf(stderr,
		    "Warning: %s option is no longer supported.\n",
		    arg);

	} else {
	    /*
	     * Pass along anything that's not explicitly recognized.
	     */
	    AddOption(arg, NULL);
	}
    }

    if (--argc >= 0) {
	if (jarflag) {
	    *pjarfile = *argv++;
	    *pclassname = 0;
	} else {
	    *pjarfile = 0;
	    *pclassname = *argv++;
	}

	*pargc = argc;
	*pargv = argv;
    }

    return JNI_TRUE;
}

/*
 * Initializes the Java Virtual Machine.  Also frees options array when
 * finished.
 */
static jboolean
InitializeJVM(JavaVM **pvm, JNIEnv **penv, InvocationFunctions *ifn)
{
    JavaVMInitArgs args;
    jint r;

#ifdef OLDJAVA
    /* Indicate that we are using the old-style launcher */
    AddOption("-Xoldjava", NULL);
#endif

    memset(&args, 0, sizeof(args));
    args.version  = JNI_VERSION_1_2;
    args.nOptions = numOptions;
    args.options  = options;
    args.ignoreUnrecognized = JNI_FALSE;

    if (debug) {
	int i = 0;
	printf("JavaVM args:\n    ");
	printf("version 0x%08lx, ", args.version);
	printf("ignoreUnrecognized is %s, ",
	       args.ignoreUnrecognized ? "JNI_TRUE" : "JNI_FALSE");
	printf("nOptions is %ld\n", args.nOptions);
	for (i = 0; i < numOptions; i++)
	    printf("    option[%2d] = '%s'\n", 
		   i, args.options[i].optionString);
    }

    r = ifn->CreateJavaVM(pvm, (void **)penv, &args);
    free(options);
    return r == JNI_OK;
}

#define NULL_CHECK0(e) if ((e) == 0) return 0
#define NULL_CHECK(e) if ((e) == 0) return

/*
 * Returns a pointer to a block of at least 'size' bytes of memory.
 * Prints error message and exits if the memory could not be allocated.
 */
static void *
MemAlloc(size_t size)
{
    void *p = malloc(size);
    if (p == 0) {
	perror("malloc");
	exit(1);
    }
    return p;
}

/*
 * Returns a new Java string object for the specified platform string.
 */
static jstring
NewPlatformString(JNIEnv *env, char *s)
{
    int len = strlen(s);
    jclass cls;
    jmethodID mid;
    jbyteArray ary;

    NULL_CHECK0(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "<init>", "([B)V"));
    ary = (*env)->NewByteArray(env, len);
    if (ary != 0) {
	jstring str = 0;
	(*env)->SetByteArrayRegion(env, ary, 0, len, (jbyte *)s);
	if (!(*env)->ExceptionOccurred(env)) {
	    str = (*env)->NewObject(env, cls, mid, ary);
	}
	(*env)->DeleteLocalRef(env, ary);
	return str;
    }
    return 0;
}

/*
 * Returns a new array of Java string objects for the specified
 * array of platform strings.
 */
static jobjectArray
NewPlatformStringArray(JNIEnv *env, char **strv, int strc)
{
    jarray cls;
    jarray ary;
    int i;

    NULL_CHECK0(cls = (*env)->FindClass(env, "java/lang/String"));
    NULL_CHECK0(ary = (*env)->NewObjectArray(env, strc, cls, 0));
    for (i = 0; i < strc; i++) {
	jstring str = NewPlatformString(env, *strv++);
	NULL_CHECK0(str);
	(*env)->SetObjectArrayElement(env, ary, i, str);
	(*env)->DeleteLocalRef(env, str);
    }
    return ary;
}

/*
 * Loads a class, convert the '.' to '/'.
 */
static jclass
LoadClass(JNIEnv *env, char *name)
{
    char *buf = MemAlloc(strlen(name) + 1);
    char *s = buf, *t = name, c;
    jclass cls;
    jlong start, end;

    if (debug)
	start = CounterGet();

    do {
        c = *t++;
	*s++ = (c == '.') ? '/' : c;
    } while (c != '\0');
    cls = (*env)->FindClass(env, buf);
    free(buf);

    if (debug) {
	end   = CounterGet();
	printf("%ld micro seconds to load main class\n",
	       (jint)Counter2Micros(end-start));
	printf("----_JAVA_LAUNCHER_DEBUG----\n");
    }

    return cls;
}

/*
 * Returns the main class name for the specified jar file.
 */
static jstring
GetMainClassName(JNIEnv *env, char *jarname)
{
#define MAIN_CLASS "Main-Class"
    jclass cls;
    jmethodID mid;
    jobject jar, man, attr;
    jstring str, result = 0;
    
    NULL_CHECK0(cls = (*env)->FindClass(env, "java/util/jar/JarFile"));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "<init>",
					  "(Ljava/lang/String;)V"));
    NULL_CHECK0(str = NewPlatformString(env, jarname));
    NULL_CHECK0(jar = (*env)->NewObject(env, cls, mid, str));
    NULL_CHECK0(mid = (*env)->GetMethodID(env, cls, "getManifest",
					  "()Ljava/util/jar/Manifest;"));
    man = (*env)->CallObjectMethod(env, jar, mid);
    if (man != 0) {
	NULL_CHECK0(mid = (*env)->GetMethodID(env,
				    (*env)->GetObjectClass(env, man),
				    "getMainAttributes",
				    "()Ljava/util/jar/Attributes;"));
	attr = (*env)->CallObjectMethod(env, man, mid);
	if (attr != 0) {
	    NULL_CHECK0(mid = (*env)->GetMethodID(env,
				    (*env)->GetObjectClass(env, attr),
				    "getValue",
				    "(Ljava/lang/String;)Ljava/lang/String;"));
	    NULL_CHECK0(str = NewPlatformString(env, MAIN_CLASS));
	    result = (*env)->CallObjectMethod(env, attr, mid, str);
	}
    }
    return result;
}

#ifdef JAVA_ARGS
static char *java_args[] = JAVA_ARGS;
static char *app_classpath[] = APP_CLASSPATH;
#define NUM_ARGS (sizeof(java_args) / sizeof(char *))
#define NUM_APP_CLASSPATH (sizeof(app_classpath) / sizeof(char *))

/*
 * For tools convert 'javac -J-ms32m' to 'java -ms32m ...'
 */
static void
TranslateDashJArgs(int *pargc, char ***pargv)
{
    int argc = *pargc;
    char **argv = *pargv;
    int nargc = argc + NUM_ARGS;
    char **nargv = MemAlloc((nargc + 1) * sizeof(char *));
    int i;

    *pargc = nargc;
    *pargv = nargv;

    /* Copy the VM arguments (i.e. prefixed with -J) */
    for (i = 0; i < NUM_ARGS; i++) {
	char *arg = java_args[i];
	if (arg[0] == '-' && arg[1] == 'J')
	    *nargv++ = arg + 2;
    }

    for (i = 0; i < argc; i++) {
	char *arg = argv[i];
	if (arg[0] == '-' && arg[1] == 'J')
	    *nargv++ = arg + 2;
    }

    /* Copy the rest of the arguments */
    for (i = 0; i < NUM_ARGS; i++) {
	char *arg = java_args[i];
	if (arg[0] != '-' || arg[1] != 'J') {
	    *nargv++ = arg;
	}
    }
    for (i = 0; i < argc; i++) {
	char *arg = argv[i];
	if (arg[0] != '-' || arg[1] != 'J') {
	    *nargv++ = arg;
	}
    }
    *nargv = 0;
}

/*
 * For our tools, we try to add 3 VM options:
 *	-Denv.class.path=<envcp>
 *	-Dapplication.home=<apphome>
 *	-Djava.class.path=<appcp>
 * <envcp>   is the user's setting of CLASSPATH -- for instance the user
 *           tells javac where to find binary classes through this environment
 *           variable.  Notice that users will be able to compile against our
 *           tools classes (sun.tools.javac.Main) only if they explicitly add
 *           tools.jar to CLASSPATH.
 * <apphome> is the directory where the application is installed.
 * <appcp>   is the classpath to where our apps' classfiles are.
 */
static jboolean
AddApplicationOptions()
{
    char *s, *envcp, *appcp, *apphome;
    char home[MAXPATHLEN]; /* application home */
    char separator[] = { PATH_SEPARATOR, '\0' };
    int size, i;

    s = getenv("CLASSPATH");
    if (s) {
	/* 40 for -Denv.class.path= */
	envcp = (char *)MemAlloc(strlen(s) + 40);
	sprintf(envcp, "-Denv.class.path=%s", s);
	AddOption(envcp, NULL);
    }

    if (!GetApplicationHome(home, sizeof(home))) {
	fprintf(stderr, "Can't determine application home\n");
	return JNI_FALSE;
    }

    /* 40 for '-Dapplication.home=' */
    apphome = (char *)MemAlloc(strlen(home) + 40);
    sprintf(apphome, "-Dapplication.home=%s", home);
    AddOption(apphome, NULL);

    /* How big is the application's classpath? */
    size = strlen(home) * NUM_APP_CLASSPATH + 40; /* 40: -Djava.class.path */
    for (i = 0; i < NUM_APP_CLASSPATH; i++) {
	size += strlen(app_classpath[i]);
    }
    appcp = (char *)MemAlloc(size + 1);
    strcpy(appcp, "-Djava.class.path=");
    for (i = 0; i < NUM_APP_CLASSPATH; i++) {
	strcat(appcp, home);			/* c:\program files\myapp */
	strcat(appcp, app_classpath[i]);	/* lib\myapp.jar	  */
	strcat(appcp, separator);		/* ;			  */
    }
    appcp[strlen(appcp)-1] = '\0';  /* remove trailing path seperator */
    AddOption(appcp, NULL);
    return JNI_TRUE;
}
#endif

/*
 * Prints the version information from the java.version and other properties.
 */
static void
PrintJavaVersion(JNIEnv *env)
{
    jclass sysClass;
    jmethodID getPropID;

    jstring java_version;
    jstring java_vm_name;
    jstring java_vm_info;

    char c_version[128];
    char c_vm_name[256];
    char c_vm_info[256];

    NULL_CHECK(sysClass = (*env)->FindClass(env, "java/lang/System"));
    NULL_CHECK(getPropID = (*env)->GetStaticMethodID(env, sysClass,
						     "getProperty",
			     "(Ljava/lang/String;)Ljava/lang/String;"));

    NULL_CHECK(java_version = (*env)->NewStringUTF(env, "java.version"));
    NULL_CHECK(java_vm_name = (*env)->NewStringUTF(env, "java.vm.name"));
    NULL_CHECK(java_vm_info = (*env)->NewStringUTF(env, "java.vm.info"));

    NULL_CHECK(java_version = 
    (*env)->CallStaticObjectMethod(env, sysClass, getPropID, java_version));
    NULL_CHECK(java_vm_name = 
    (*env)->CallStaticObjectMethod(env, sysClass, getPropID, java_vm_name));
    NULL_CHECK(java_vm_info =
    (*env)->CallStaticObjectMethod(env, sysClass, getPropID, java_vm_info));

    (*env)->GetStringUTFRegion(env, java_version, 0,
			       (*env)->GetStringLength(env, java_version),
			       c_version);
    (*env)->GetStringUTFRegion(env, java_vm_name, 0,
			       (*env)->GetStringLength(env, java_vm_name),
			       c_vm_name);
    (*env)->GetStringUTFRegion(env, java_vm_info, 0,
			       (*env)->GetStringLength(env, java_vm_info),
			       c_vm_info);

    fprintf(stderr, "java version \"%s\"\n", c_version);
    fprintf(stderr, "%s (%s)\n", c_vm_name, c_vm_info);
}

/*
 * Prints default usage message.
 */
static void PrintUsage(void)
{
    fprintf(stdout,
	"Usage: %s [-options] class [args...]\n"
#ifndef OLDJAVA
	"           (to execute a class)\n"
	"   or  %s -jar [-options] jarfile [args...]\n"
	"           (to execute a jar file)\n"
#endif
	"\n"
	"where options include:\n"
#ifdef OLDJAVA
	"    -cp -classpath <directories and zip/jar files separated by %c>\n"
	"              set search path for classes and resources\n"
#else
	"    -cp -classpath <directories and zip/jar files separated by %c>\n"
	"              set search path for application classes and resources\n"
#endif
	"    -D<name>=<value>\n"
	"              set a system property\n"
	"    -verbose[:class|gc|jni]\n"
	"              enable verbose output\n"
	"    -version  print product version\n"
	"    -? -help  print this help message\n"
	"    -X        print help on non-standard options\n",
#ifndef OLDJAVA
	progname,
#endif
	progname, PATH_SEPARATOR
    );
}

/*
 * Print usage message for -X options.
 */
static jint
PrintXUsage(void)
{
    JVM_PrintXUsage();
    return 0;
}
