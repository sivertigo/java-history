/*
 * @(#)Compiler.java	1.16 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang;

/**
 * The <code>Compiler</code> class is provided to support
 * Java-to-native-code compilers and related services. By design, the
 * <code>Compiler</code> class does nothing; it serves as a
 * placeholder for a JIT compiler implementation.
 * <p>
 * When the Java Virtual Machine first starts, it determines if the
 * system property <code>java.compiler</code> exists. (System
 * properties are accessible through <code>getProperty</code> and, 
 * a method defined by the <code>System</code> class.) If so, and the
 * name isn't <b>NONE</b> or <b>none</b>, the internal JIT is enabled.
 * <p>
 * If no compiler is available, these methods do nothing.
 *
 * @author  Frank Yellin
 * @version 1.16, 11/29/01
 * @see     java.lang.System#getProperty(java.lang.String)
 * @see     java.lang.System#getProperty(java.lang.String, java.lang.String)
 * @since   JDK1.0
 */
public final class Compiler  {
    private Compiler() {}		// don't make instances

    private static native void registerNatives();

    static {
        registerNatives();
	String library = null;
	boolean loaded = false;
	library = System.getProperty("java.compiler");
	if (library == null ||
	    library.equalsIgnoreCase("none") ||
	    library.equals("")) {
	    // -- NO JIT --
	    loaded = false;
	}
	else {
	    if (!library.equals("sunwjit")) {
		System.err.println("Warning: invalid JIT compiler. " +
				   "Choices are sunwjit or NONE.\n" +
				   "Will use sunwjit.");
	    }
	    loaded = true;
	}
	String vmInfo = System.getProperty("java.vm.info");
	if (loaded) {
	    System.setProperty("java.vm.info", vmInfo + ", " + library);
	} else {
	    System.setProperty("java.vm.info", vmInfo + ", nojit");
	}
    }

    /**
     * Compiles the specified class.
     *
     * @param   clazz   a class.
     * @return  <code>true</code> if the compilation succeeded;
     *          <code>false</code> if the compilation failed or no compiler
     *          is available.
     */
    public static native boolean compileClass(Class clazz);

    /**
     * Compiles all classes whose name matches the specified string.
     *
     * @param   string   the name of the classes to compile.
     * @return  <code>true</code> if the compilation succeeded;
     *          <code>false</code> if the compilation failed or no compiler
     *          is available.
     */
    public static native boolean compileClasses(String string);

    /**
     * Examines the argument type and its fields and perform some documented
     * operation. No specific operations are required.
     *
     * @param   any   an argument.
     * @return  a compiler-specific value, or <code>null</code> if no compiler
     *          is available.
     */
    public static native Object command(Object any);

    /**
     * Cause the Compiler to resume operation. (This a noop on Solaris).
     */
    public static native void enable();

    /**
     * Cause the Compiler to cease operation.  (This a noop on Solaris).
     */
    public static native void disable();
}
