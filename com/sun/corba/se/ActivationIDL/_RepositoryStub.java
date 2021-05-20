package com.sun.corba.se.ActivationIDL;


/**
* com/sun/corba/se/ActivationIDL/_RepositoryStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.1"
* from ../../../../../../src/share/classes/com/sun/corba/se/ActivationIDL/activation.idl
* Friday, February 8, 2008 7:37:14 PM PST
*/

public class _RepositoryStub extends org.omg.CORBA.portable.ObjectImpl implements com.sun.corba.se.ActivationIDL.Repository
{


  // always uninstalled.
  public int registerServer (com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef serverDef) throws com.sun.corba.se.ActivationIDL.ServerAlreadyRegistered, com.sun.corba.se.ActivationIDL.BadServerDefinition
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("registerServer", true);
                com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDefHelper.write ($out, serverDef);
                $in = _invoke ($out);
                int $result = com.sun.corba.se.ActivationIDL.ServerIdHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerAlreadyRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerAlreadyRegisteredHelper.read ($in);
                else if (_id.equals ("IDL:ActivationIDL/BadServerDefinition:1.0"))
                    throw com.sun.corba.se.ActivationIDL.BadServerDefinitionHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return registerServer (serverDef        );
            } finally {
                _releaseReply ($in);
            }
  } // registerServer


  // unregister server definition
  public void unregisterServer (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("unregisterServer", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
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
                unregisterServer (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // unregisterServer


  // get server definition
  public com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef getServer (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getServer", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDef $result = com.sun.corba.se.ActivationIDL.RepositoryPackage.ServerDefHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getServer (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // getServer


  // Return whether the server has been installed
  public boolean isInstalled (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("isInstalled", true);
                com.sun.corba.se.ActivationIDL.ServerIdHelper.write ($out, serverId);
                $in = _invoke ($out);
                boolean $result = $in.read_boolean ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return isInstalled (serverId        );
            } finally {
                _releaseReply ($in);
            }
  } // isInstalled


  // if the server is currently marked as installed.
  public void install (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerAlreadyInstalled
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


  // if the server is currently marked as uninstalled.
  public void uninstall (int serverId) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered, com.sun.corba.se.ActivationIDL.ServerAlreadyUninstalled
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


  // list registered servers
  public int[] listRegisteredServers ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("listRegisteredServers", true);
                $in = _invoke ($out);
                int $result[] = com.sun.corba.se.ActivationIDL.ServerIdsHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return listRegisteredServers (        );
            } finally {
                _releaseReply ($in);
            }
  } // listRegisteredServers


  // servers.
  public String[] getApplicationNames ()
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getApplicationNames", true);
                $in = _invoke ($out);
                String $result[] = com.sun.corba.se.ActivationIDL.RepositoryPackage.StringSeqHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getApplicationNames (        );
            } finally {
                _releaseReply ($in);
            }
  } // getApplicationNames


  // Find the ServerID associated with the given application name.
  public int getServerID (String applicationName) throws com.sun.corba.se.ActivationIDL.ServerNotRegistered
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getServerID", true);
                $out.write_string (applicationName);
                $in = _invoke ($out);
                int $result = com.sun.corba.se.ActivationIDL.ServerIdHelper.read ($in);
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                if (_id.equals ("IDL:ActivationIDL/ServerNotRegistered:1.0"))
                    throw com.sun.corba.se.ActivationIDL.ServerNotRegisteredHelper.read ($in);
                else
                    throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getServerID (applicationName        );
            } finally {
                _releaseReply ($in);
            }
  } // getServerID

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:ActivationIDL/Repository:1.0"};

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
} // class _RepositoryStub
