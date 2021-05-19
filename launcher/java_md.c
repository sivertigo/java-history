/*
 * @(#)java_md.c	1.10 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

#include "java.h"
#include <dlfcn.h>
#include <string.h>
#include <limits.h>

#ifdef DEBUG
#define JVM_DLL "libjvm_g.so"
#else
#define JVM_DLL "libjvm.so"
#endif

/*
 * Load JVM of "jvmtype", and intialize the invocation functions.  On
 * Solaris, currently, "jvmtype" is ignored and the VM used depends on
 * LD_LIBRARY_PATH.
 */
jboolean
LoadJavaVM(char *jvmtype, InvocationFunctions *ifn)
{
    ifn->CreateJavaVM = JNI_CreateJavaVM;
    ifn->GetDefaultJavaVMInitArgs = JNI_GetDefaultJavaVMInitArgs;
    return JNI_TRUE;
}

#if	0
/*
 * Get the path to the file that has the usage message for -X options.
 *
 * N.B. Not used in this JVM implementation (which has chosen to stick with
 *	the OptionDesc scheme -- see JVM_PrintXUsage()).
 */
void
GetXUsagePath(char *buf, jint bufsize)
{
    Dl_info dlinfo;
   
    dladdr(dlsym(dlopen(JVM_DLL, RTLD_LAZY), "JNI_CreateJavaVM"), &dlinfo);
    strncpy(buf, (char *)dlinfo.dli_fname, bufsize - 1);
    buf[bufsize] = '\0';
    *(strrchr(buf, '/')) = '\0';
    strcat(buf, "/Xusage.txt");
}
#endif

/*
 * If app is "/foo/bin/sparc/green_threads/javac", then put "/foo" into buf.
 */
jboolean
GetApplicationHome(char *buf, jint bufsize)
{
#ifdef USE_APPHOME
    char *apphome = getenv("APPHOME");
    if (apphome) {
	strncpy(buf, apphome, bufsize-1);
	buf[bufsize-1] = '\0';
	return JNI_TRUE;
    } else {
	return JNI_FALSE;
    }
#else
    Dl_info dlinfo;

    dladdr((void *)GetApplicationHome, &dlinfo);
    strncpy(buf, dlinfo.dli_fname, bufsize - 1);
    buf[bufsize-1] = '\0';
    
    *(strrchr(buf, '/')) = '\0';  /* executable file      */
    *(strrchr(buf, '/')) = '\0';  /* green|native_threads */
    *(strrchr(buf, '/')) = '\0';  /* sparc|i386           */
    *(strrchr(buf, '/')) = '\0';  /* bin                  */
    return JNI_TRUE;
#endif
}
