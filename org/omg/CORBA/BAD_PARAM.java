/*
 * @(#)BAD_PARAM.java	1.24 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CORBA;

/**
 * The CORBA <code>BAD_PARAM</code> exception, which is thrown
 * when an invalid parameter is passed to a method.
 * It contains a minor code, which gives more detailed information about
 * what caused the exception, and a completion status. It may also contain
 * a string describing the exception.
 *
 * @see <A href="../../../../guide/idl/jidlExceptions.html">documentation on
 * Java&nbsp;IDL exceptions</A>
 * @see <A href="../../../../guide/idl/jidlExceptions.html#minorcodemeanings">meaning of
 * minor codes</A>
 * @version     1.18, 09/09/97
 * @since       JDK1.2
 */

public final class BAD_PARAM extends SystemException {

    /**
     * Constructs a <code>BAD_PARAM</code> exception with a default
	 * minor code of 0 and a completion state of COMPLETED_NO.
     */
    public BAD_PARAM() {
        this("");
    }

    /**
     * Constructs a <code>BAD_PARAM</code> exception with the specified detail
	 * message, a minor code of 0, and a completion state of COMPLETED_NO.
	 *
     * @param s the String containing a detail message describing this 
	 *          exception
     */
    public BAD_PARAM(String s) {
        this(s, 0, CompletionStatus.COMPLETED_NO);
    }

    /**
     * Constructs a <code>BAD_PARAM</code> exception with the specified
     * minor code and completion status.
     * @param minor the minor code
     * @param completed the completion status
     */
    public BAD_PARAM(int minor, CompletionStatus completed) {
        this("", minor, completed);
    }

    /**
     * Constructs a <code>BAD_PARAM</code> exception with the specified detail
     * message, minor code, and completion status.
     * A detail message is a <code>String</code> that describes 
	 * this particular exception.
	 *
     * @param s the <code>String</code> containing a detail message
     * @param minor the minor code
     * @param completed the completion status
     */
    public BAD_PARAM(String s, int minor, CompletionStatus completed) {
        super(s, minor, completed);
    }
}
