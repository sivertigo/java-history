/*
 * @(#)Types.java	1.6 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.sql;

/**
 * <P>This class defines constants that are used to identify SQL types.
 * The actual type constant values are equivalent to those in XOPEN.
 *
 */
public class Types {

	public final static int BIT 		=  -7;
	public final static int TINYINT 	=  -6;
	public final static int SMALLINT	=   5;
	public final static int INTEGER 	=   4;
	public final static int BIGINT 		=  -5;

	public final static int FLOAT 		=   6;
	public final static int REAL 		=   7;
	public final static int DOUBLE 		=   8;

	public final static int NUMERIC 	=   2;
	public final static int DECIMAL		=   3;

	public final static int CHAR		=   1;
	public final static int VARCHAR 	=  12;
	public final static int LONGVARCHAR 	=  -1;

	public final static int DATE 		=  91;
	public final static int TIME 		=  92;
	public final static int TIMESTAMP 	=  93;

	public final static int BINARY		=  -2;
	public final static int VARBINARY 	=  -3;
	public final static int LONGVARBINARY 	=  -4;

	public final static int NULL		=   0;

    /**
     * OTHER indicates that the SQL type is database specific and
     * gets mapped to a Java object which can be accessed via
     * getObject and setObject.
     */
	public final static int OTHER		= 1111;

    // Prevent instantiation
    private Types() {}
}
