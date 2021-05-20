package com.sun.corba.se.ActivationIDL;


/**
* com/sun/corba/se/ActivationIDL/_ServerManagerStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../../../src/share/classes/com/sun/corba/se/ActivationIDL/activation.idl
* Friday, February 8, 2008 7:37:14 PM PST
*/

public class _ServerManagerStub extends org.omg.CORBA.portable.ObjectImpl implements com.sun.corba.se.ActivationIDL.ServerManager
{


  // A new ORB started server registers itself with the Activator
  public void active (int serverId, com.sun.corba.se.ActivationIDL.Server serverObj) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("active", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                com.sun.corba.se.ActivationIDL.ServerHelper.write ($out, serverObj);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                active (serverId, serverObj        );
            } finally {
                _releaseReply ($in);
            }
  } // active


  // Install a particular kind of endpoint
  public void registerEndpoints (int serverId, String orbId, com.sun.corba.se.ActivationIDL.EndPointInfo[] endPointInfo) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.NoSuchEndPoint, com.sun.corba.se.ActivationIDL.ORBAlreadyRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registerEndpoints", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                com.sun.corba.se.ActivationIDL.ORBidHelper.write ($out, orbId);
                com.sun.corba.se.ActivationIDL.EndpointInfoListHelper.write ($out, endPointInfo);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.ActivationIDL.NoSuchEndPointHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ORBAlreadyRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ORBAlreadyRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                registerEndpoints (serverId, orbId, endPointInfo        );
            } finally {
                _releaseReply ($in);
            }
  } // registerEndpoints


  // list active servers
  public int[] getActiveServers ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getActiveServers", true);
                $in = _invoke ($out);
                int $result[] = com.sun.corba.se.ActivationIDL.ServerIdsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getActiveServers (        );
            } finally {
                _releaseReply ($in);
            }
  } // getActiveServers


  // If the server is not running, start it up.
  public void activate (int serverId) throws com.sun.corba.se.ActivationIDL.ServerAlreadyActive, com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("activate", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerAlreadyActive:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerAlreadyActiveHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerHeldDownHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                activate (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // activate


  // If the server is running, shut it down
  public void shutdown (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotActive, com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("shutdown", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotActive:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotActiveHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                shutdown (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // shutdown


  // currently running, this method will activate it.
  public void install (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown, com.sun.corba.se.ActivationIDL.ServerAlreadyInstalled
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("install", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerHeldDownHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerAlreadyInstalled:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerAlreadyInstalledHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                install (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // install


  // list all registered ORBs for a server
  public String[] getORBNames (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getORBNames", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                String $result[] = com.sun.corba.se.ActivationIDL.ORBidListHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getORBNames (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // getORBNames


  // After this hook completes, the server may still be running.
  public void uninstall (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown, com.sun.corba.se.ActivationIDL.ServerAlreadyUninstalled
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("uninstall", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                return;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerHeldDownHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerAlreadyUninstalled:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerAlreadyUninstalledHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                uninstall (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // uninstall


  // Starts the server if it is not already running.
  public com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocation locateServer (int serverId, String endPoint) throws com.sun.corba.se.ActivationIDL.NoSuchEndPoint, com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("locateServer", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $out.write_string (endPoint);
                $in = _invoke ($out);
                com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocation $result = com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.ActivationIDL.NoSuchEndPointHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerHeldDownHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return locateServer (serverId, endPoint        );
            } finally {
                _releaseReply ($in);
            }
  } // locateServer


  // Starts the server if it is not already running.
  public com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORB locateServerForORB (int serverId, String orbId) throws com.sun.corba.se.ActivationIDL.InvalidORBid, com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerHeldDown
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("locateServerForORB", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                com.sun.corba.se.ActivationIDL.ORBidHelper.write ($out, orbId);
                $in = _invoke ($out);
                com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORB $result = com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORBHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/InvalidORBid:1.0"))
                    throw com.sun.corba.se.ActivationIDL.InvalidORBidHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/ServerHeldDown:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerHeldDownHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return locateServerForORB (serverId, orbId        );
            } finally {
                _releaseReply ($in);
            }
  } // locateServerForORB


  // get the port for the endpoint of the locator
  public int getEndpoint (String endPointType) throws com.sun.corba.se.ActivationIDL.NoSuchEndPoint
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getEndpoint", true);
                $out.write_string (endPointType);
                $in = _invoke ($out);
                int $result = com.sun.corba.se.ActivationIDL.TCPPortHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.ActivationIDL.NoSuchEndPointHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getEndpoint (endPointType        );
            } finally {
                _releaseReply ($in);
            }
  } // getEndpoint


  // to pick a particular port type.
  public int getServerPortForType (com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORB location, String endPointType) throws com.sun.corba.se.ActivationIDL.NoSuchEndPoint
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getServerPortForType", true);
                com.sun.corba.se.ActivationIDL.LocatorPackage.ServerLocationPerORBHelper.write ($out, location);
                $out.write_string (endPointType);
                $in = _invoke ($out);
                int $result = com.sun.corba.se.ActivationIDL.TCPPortHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/NoSuchEndPoint:1.0"))
                    throw com.sun.corba.se.ActivationIDL.NoSuchEndPointHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getServerPortForType (location, endPointType        );
            } finally {
                _releaseReply ($in);
            }
  } // getServerPortForType

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ActivationIDL/ServerManager:1.0", 
    "IDL:ActivationIDL/Activator:1.0", 
    "IDL:ActivationIDL/Locator:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.Object obj = org.omg.CORBA.ORB.init (args, props).string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     String str = org.omg.CORBA.ORB.init (args, props).object_to_string (this);
     s.writeUTF (str);
  }
} // class _ServerManagerStub
