/*
 * @(#)DynArray.java	1.4 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */


package org.omg.CORBA;


/** The DynArray interface represents a DynAny object which is associated
 *  with an array.
 */

public interface DynArray extends org.omg.CORBA.Object, org.omg.CORBA.DynAny
{
    /**
     * Return the value of all the elements of the array.
     *
     * @return an array of <code>Any</code>s.
     */
    public org.omg.CORBA.Any[] get_elements();

    /**
     * Set the values of all elements of an array represented by this
     * <code>DynArray</code>.
     *
     * @param value the array of <code>Any</code>s.
     * @exception InvalidSeq if the sequence is bad.
     */
    public void set_elements(org.omg.CORBA.Any[] value)
        throws org.omg.CORBA.DynAnyPackage.InvalidSeq;
}
