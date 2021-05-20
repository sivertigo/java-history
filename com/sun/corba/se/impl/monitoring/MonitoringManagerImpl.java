/*
 * @(#)MonitoringManagerImpl.java	1.4 08/10/03
 * 
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.corba.se.impl.monitoring;

import com.sun.corba.se.spi.monitoring.MonitoringManager;
import com.sun.corba.se.spi.monitoring.MonitoringManagerFactory;
import com.sun.corba.se.spi.monitoring.MonitoredObject;
import com.sun.corba.se.spi.monitoring.MonitoredObjectFactory;
import com.sun.corba.se.spi.monitoring.MonitoringFactories;

public class MonitoringManagerImpl implements MonitoringManager {
    private final MonitoredObject rootMonitoredObject;

    MonitoringManagerImpl( String nameOfTheRoot, String description ) {
        MonitoredObjectFactory f = 
            MonitoringFactories.getMonitoredObjectFactory();
        rootMonitoredObject = 
            f.createMonitoredObject( nameOfTheRoot, description );
    }

    public void clearState( ) {
        rootMonitoredObject.clearState( );
    }

    public MonitoredObject getRootMonitoredObject( ) {
        return rootMonitoredObject;
    }

    public void close() {
        MonitoringManagerFactory f = 
            MonitoringFactories.getMonitoringManagerFactory();
        f.remove( rootMonitoredObject.getName() ) ;
    }
}
        
    

   

