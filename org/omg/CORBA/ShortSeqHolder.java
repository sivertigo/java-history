/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package org.omg.CORBA;


/**
* org/omg/CORBA/ShortSeqHolder.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from streams.idl
* 13 May 1999 22:41:36 o'clock GMT+00:00
*/

public final class ShortSeqHolder implements org.omg.CORBA.portable.Streamable
{
    public short value[] = null;

    public ShortSeqHolder ()
    {
    }

    public ShortSeqHolder (short[] initialValue)
    {
	value = initialValue;
    }

    public void _read (org.omg.CORBA.portable.InputStream i)
    {
	value = org.omg.CORBA.ShortSeqHelper.read (i);
    }

    public void _write (org.omg.CORBA.portable.OutputStream o)
    {
	org.omg.CORBA.ShortSeqHelper.write (o, value);
    }

    public org.omg.CORBA.TypeCode _type ()
    {
	return org.omg.CORBA.ShortSeqHelper.type ();
    }

}
