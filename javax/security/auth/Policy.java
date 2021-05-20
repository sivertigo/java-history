/*
 * @(#)Policy.java	1.48 03/01/23
 *
 * Copyright 2003 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
 
package javax.security.auth;

/**
 * <p> This is an abstract class for representing the system policy for
 * Subject-based authorization.  A subclass implementation
 * of this class provides a means to specify a Subject-based
 * access control <code>Policy</code>.
 *
 * <p> A <code>Policy</code> object can be queried for the set of
 * Permissions granted to code running as a 
 * <code>Principal</code> in the following manner:
 *
 * <pre>
 *	policy = Policy.getPolicy();
 *	PermissionCollection perms = policy.getPermissions(subject,
 *							codeSource);
 * </pre>
 *
 * The <code>Policy</code> object consults the local policy and returns
 * and appropriate <code>Permissions</code> object with the
 * Permissions granted to the Principals associated with the
 * provided <i>subject</i>, and granted to the code specified
 * by the provided <i>codeSource</i>.
 *
 * <p> A <code>Policy</code> contains the following information.
 * Note that this example only represents the syntax for the default
 * <code>Policy</code> implementation. Subclass implementations of this class
 * may implement alternative syntaxes and may retrieve the
 * <code>Policy</code> from any source such as files, databases,
 * or servers.
 *
 * <p> Each entry in the <code>Policy</code> is represented as
 * a <b><i>grant</i></b> entry.  Each <b><i>grant</i></b> entry
 * specifies a codebase, code signers, and Principals triplet,
 * as well as the Permissions granted to that triplet.
 *
 * <pre>
 *	grant CodeBase ["URL"], Signedby ["signers"],
 *	      Principal [Principal_Class] "Principal_Name" {
 *	    Permission Permission_Class ["Target_Name"]
 *					[, "Permission_Actions"]
 *					[, signedBy "SignerName"];
 *	};
 * </pre>
 *
 * The CodeBase and Signedby components of the triplet name/value pairs
 * are optional.  If they are not present, then any any codebase will match,
 * and any signer (including unsigned code) will match.
 * For Example,
 *
 * <pre>
 *	grant CodeBase "foo.com", Signedby "foo",
 *	      Principal com.sun.security.auth.SolarisPrincipal "duke" {
 *	    permission java.io.FilePermission "/home/duke", "read, write";
 *	};
 * </pre>
 *
 * This <b><i>grant</i></b> entry specifies that code from "foo.com",
 * signed by "foo', and running as a <code>SolarisPrincipal</code> with the
 * name, duke, has one <code>Permission</code>.  This <code>Permission</code>
 * permits the executing code to read and write files in the directory,
 * "/home/duke".
 *
 * <p> To "run" as a particular <code>Principal</code>,
 * code invokes the <code>Subject.doAs(subject, ...)</code> method.
 * After invoking that method, the code runs as all the Principals
 * associated with the specified <code>Subject</code>.
 * Note that this <code>Policy</code> (and the Permissions
 * granted in this <code>Policy</code>) only become effective
 * after the call to <code>Subject.doAs</code> has occurred.
 *
 * <p> Multiple Principals may be listed within one <b><i>grant</i></b> entry.
 * All the Principals in the grant entry must be associated with
 * the <code>Subject</code> provided to <code>Subject.doAs</code>
 * for that <code>Subject</code> to be granted the specified Permissions.
 *
 * <pre>
 *	grant Principal com.sun.security.auth.SolarisPrincipal "duke",
 *	      Principal com.sun.security.auth.SolarisNumericUserPrincipal "0" {
 *	    permission java.io.FilePermission "/home/duke", "read, write";
 *	    permission java.net.SocketPermission "duke.com", "connect";
 *	};
 * </pre>
 *
 * This entry grants any code running as both "duke" and "0"
 * permission to read and write files in duke's home directory,
 * as well as permission to make socket connections to "duke.com".
 * 
 * <p> Note that non Principal-based grant entries are not permitted
 * in this <code>Policy</code>.  Therefore, grant entries such as:
 *
 * <pre>
 *	grant CodeBase "foo.com", Signedby "foo" {
 *	    permission java.io.FilePermission "/tmp/scratch", "read, write";
 *	};
 * </pre>
 *
 * are rejected.  Such permission must be listed in the
 * <code>java.security.Policy</code>.
 *
 * <p> The default <code>Policy</code> implementation can be changed by
 * setting the value of the "auth.policy.provider" security property
 * (in the Java security properties file) to the fully qualified name of
 * the desired <code>Policy</code> implementation class.
 * The Java security properties file is located in the file named
 * &lt;JAVA_HOME&gt;/lib/security/java.security, where &lt;JAVA_HOME&gt;
 * refers to the directory where the JDK was installed.
 *
 * @deprecated	as of JDK version 1.4 -- Replaced by java.security.Policy.
 *		java.security.Policy has a method:
 * <pre>
 * 	public PermissionCollection getPermissions
 *  	    (java.security.ProtectionDomain pd)
 * 
 * </pre>
 * and ProtectionDomain has a constructor:
 * <pre>
 *	public ProtectionDomain
 *	    (CodeSource cs,
 *	     PermissionCollection permissions,
 *	     ClassLoader loader,
 *	     Principal[] principals)
 * </pre>
 *
 * These two APIs provide callers the means to query the
 * Policy for Principal-based Permission entries.
 *
 *
 * @version 1.48, 01/23/03
 */
public abstract class Policy {

    private static Policy policy;
    private static ClassLoader contextClassLoader;

    static {
	contextClassLoader =
		(ClassLoader)java.security.AccessController.doPrivileged
                (new java.security.PrivilegedAction() {
                public Object run() {
                    return Thread.currentThread().getContextClassLoader();
                }
        });
    };

    /**
     * Sole constructor.  (For invocation by subclass constructors, typically
     * implicit.)
     */
    protected Policy() { }

    /**
     * Returns the installed Policy object.
     * This method first calls
     * <code>SecurityManager.checkPermission</code> with the
     * <code>AuthPermission("getPolicy")</code> permission
     * to ensure the caller has permission to get the Policy object.
     *
     * <p>
     *
     * @return the installed Policy.  The return value cannot be
     *		<code>null</code>.
     *
     * @exception java.lang.SecurityException if the current thread does not
     *	    have permission to get the Policy object.
     *
     * @see #setPolicy
     */
    public static Policy getPolicy() {
	java.lang.SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(new AuthPermission("getPolicy"));
	return getPolicyNoCheck();
    }

    /**
     * Returns the installed Policy object, skipping the security check.
     *
     * @return the installed Policy.
     *
     */
    static Policy getPolicyNoCheck() {
	if (policy == null) {
 
	    synchronized(Policy.class) {
 
		if (policy == null) {
		    String policy_class = null;
		    policy_class = (String)
			java.security.AccessController.doPrivileged
			(new java.security.PrivilegedAction() {
			public Object run() {
			    return java.security.Security.getProperty
				("auth.policy.provider");
			}
		    });
		    if (policy_class == null) {
			policy_class = "com.sun.security.auth.PolicyFile";
		    }
 
		    try {
			final String finalClass = policy_class;
			policy = (Policy)
			    java.security.AccessController.doPrivileged
			    (new java.security.PrivilegedExceptionAction() {
			    public Object run() throws ClassNotFoundException,
						InstantiationException,
						IllegalAccessException {
				return Class.forName
					(finalClass,
					true,
					contextClassLoader).newInstance();
			    }
			});
		    } catch (Exception e) {
			throw new SecurityException
				(sun.security.util.ResourcesMgr.getString
				("unable to instantiate Subject-based policy"));
		    }
		}
	    }
	}
	return policy;
    }


    /**
     * Sets the system-wide Policy object. This method first calls
     * <code>SecurityManager.checkPermission</code> with the
     * <code>AuthPermission("setPolicy")</code>
     * permission to ensure the caller has permission to set the Policy.
     *
     * <p>
     *
     * @param policy the new system Policy object.
     *
     * @exception java.lang.SecurityException if the current thread does not
     *		have permission to set the Policy.
     *
     * @see #getPolicy
     */
    public static void setPolicy(Policy policy) {
	java.lang.SecurityManager sm = System.getSecurityManager();
	if (sm != null) sm.checkPermission(new AuthPermission("setPolicy"));
	Policy.policy = policy;
    }

    /**
     * Retrieve the Permissions granted to the Principals associated with
     * the specified <code>CodeSource</code>.
     *
     * <p>
     *
     * @param subject the <code>Subject</code>
     *			whose associated Principals,
     *			in conjunction with the provided
     *			<code>CodeSource</code>, determines the Permissions
     *			returned by this method.  This parameter
     *			may be <code>null</code>. <p>
     *
     * @param cs the code specified by its <code>CodeSource</code>
     *			that determines, in conjunction with the provided
     *			<code>Subject</code>, the Permissions
     *			returned by this method.  This parameter may be
     *			<code>null</code>.
     *
     * @return the Collection of Permissions granted to all the
     *			<code>Subject</code> and code specified in
     *			the provided <i>subject</i> and <i>cs</i>
     *			parameters.
     */
    public abstract java.security.PermissionCollection getPermissions
					(Subject subject,
					java.security.CodeSource cs);

    /**
     * Refresh and reload the Policy.
     *
     * <p>This method causes this object to refresh/reload its current
     * Policy. This is implementation-dependent.
     * For example, if the Policy object is stored in
     * a file, calling <code>refresh</code> will cause the file to be re-read.
     *
     * <p>
     *
     * @exception SecurityException if the caller does not have permission
     *				to refresh the Policy.
     */
    public abstract void refresh();
}
