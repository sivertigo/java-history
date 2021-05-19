/*
 * @(#)SkeletonMismatchException.java	1.8 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.rmi.server;

import java.rmi.RemoteException;

/**
 * This exception is thrown when a call is received that does not
 * match the available skeleton.  It indicates either that the
 * remote method names or signatures in this interface have changed or
 * that the stub class used to make the call and the skeleton
 * receiving the call were not generated by the same version of
 * the stub compiler (<code>rmic</code>).
 *
 * @version 1.8, 11/29/01
 * @author  Roger Riggs
 * @since   JDK1.1
 * @deprecated no replacement.  Skeletons are no longer required for remote
 * method calls in JDK1.2 and greater.
 */
public class SkeletonMismatchException extends RemoteException {

    /* indicate compatibility with JDK 1.1.x version of class */
    private static final long serialVersionUID = -7780460454818859281L;

    /**
     * Constructs a new <code>SkeletonMismatchException</code> with
     * a specified detail message.
     *
     * @param s the detail message
     * @since JDK1.1
     * @deprecated no replacement
     */
    public SkeletonMismatchException(String s) {
	super(s);
    }

}
