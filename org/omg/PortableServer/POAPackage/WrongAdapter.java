package org.omg.PortableServer.POAPackage;


/**
* org/omg/PortableServer/POAPackage/WrongAdapter.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../src/share/classes/org/omg/PortableServer/poa.idl
* Friday, February 8, 2008 7:23:14 PM GMT-08:00
*/

public final class WrongAdapter extends org.omg.CORBA.UserException
{

  public WrongAdapter ()
  {
    super(WrongAdapterHelper.id());
  } // ctor


  public WrongAdapter (String $reason)
  {
    super(WrongAdapterHelper.id() + "  " + $reason);
  } // ctor

} // class WrongAdapter
