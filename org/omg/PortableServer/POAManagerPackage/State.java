package org.omg.PortableServer.POAManagerPackage;


/**
* org/omg/PortableServer/POAManagerPackage/State.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from ../../../../src/share/classes/org/omg/PortableServer/poa.idl
* Friday, October 9, 2009 1:59:45 AM GMT-08:00
*/


/**
	 * Specifies the states for the POAManager
	 */
public class State implements org.omg.CORBA.portable.IDLEntity
{
  private        int __value;
  private static int __size = 4;
  private static org.omg.PortableServer.POAManagerPackage.State[] __array = new org.omg.PortableServer.POAManagerPackage.State [__size];

  public static final int _HOLDING = 0;
  public static final org.omg.PortableServer.POAManagerPackage.State HOLDING = new org.omg.PortableServer.POAManagerPackage.State(_HOLDING);
  public static final int _ACTIVE = 1;
  public static final org.omg.PortableServer.POAManagerPackage.State ACTIVE = new org.omg.PortableServer.POAManagerPackage.State(_ACTIVE);
  public static final int _DISCARDING = 2;
  public static final org.omg.PortableServer.POAManagerPackage.State DISCARDING = new org.omg.PortableServer.POAManagerPackage.State(_DISCARDING);
  public static final int _INACTIVE = 3;
  public static final org.omg.PortableServer.POAManagerPackage.State INACTIVE = new org.omg.PortableServer.POAManagerPackage.State(_INACTIVE);

  public int value ()
  {
    return __value;
  }

  public static org.omg.PortableServer.POAManagerPackage.State from_int (int value)
  {
    if (value >= 0 && value < __size)
      return __array[value];
    else
      throw new org.omg.CORBA.BAD_PARAM ();
  }

  protected State (int value)
  {
    __value = value;
    __array[__value] = this;
  }
} // class State
