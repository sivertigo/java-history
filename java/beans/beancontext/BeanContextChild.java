/*
 * @(#)BeanContextChild.java	1.12 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans.beancontext;

import java.beans.PropertyChangeListener;
import java.beans.VetoableChangeListener;
import java.beans.PropertyVetoException;

import java.beans.beancontext.BeanContext;

/**
 * <p>
 * JavaBeans wishing to be nested within, and obtain a reference to their
 * execution environment, or context, as defined by the BeanContext
 * sub-interface shall implement this interface.
 * </p>
 * <p>
 * Conformant BeanContexts shall as a side effect of adding a BeanContextChild
 * object shall pass a reference to itself via the setBeanContext() method of
 * this interface.
 * </p>
 * <p>
 * Note that a BeanContextChild may refuse a change in state by throwing
 * PropertyVetoedException in response.
 * </p>
 * <p>
 * In order for persistence mechanisms to function properly on BeanContextChild
 * instances across a broad variety of scenarios, implementing classes of this
 * interface are required to define as transient, any or all fields, or
 * instance variables, that may contain, or represent, references to the
 * nesting BeanContext instance or other resources obtained
 * from the BeanContext via any unspecified mechanisms.
 * </p>
 *
 * @author	Laurence P. G. Cable
 * @version	1.12
 * @since	JDK1.2
 * 
 * @seealso	java.beans.beancontext.BeanContext
 * @seealso	java.beans.PropertyChangeEvent
 * @seealso	java.beans.PropertyChangeListener
 * @seealso	java.beans.PropertyVetoEvent
 * @seealso	java.beans.PropertyVetoListener
 * @seealso	java.beans.PropertyVetoException
 */

public interface BeanContextChild {

    /**
     * <p>
     * Objects that implement this interface, 
     * shall fire a java.beans.PropertyChangeEvent, with parameters:
     *
     * @param propertyName	"beanContext"
     * @param oldValue		the previous nesting BeanContext instance, or null
     * @param newValue		the current nesting BeanContext instance, or null
     * </p>
     * <p>
     * A change in the value of the nesting BeanContext property of this
     * BeanContextChild may be vetoed by throwing the appropriate exception.
     * </p>
     */


    void setBeanContext(BeanContext bc) throws PropertyVetoException;

    /**
     * @returns the current BeanContext associated with the JavaBean
     */

    BeanContext getBeanContext();

    /**
     * add a property change listener to this bean child
     */

    void addPropertyChangeListener(String name, PropertyChangeListener pcl);

    /**
     * remove a property change listener to this bean child
     */

    void removePropertyChangeListener(String name, PropertyChangeListener pcl);

    /**
     * add a vetoable change listener to this child
     */

    void addVetoableChangeListener(String name, VetoableChangeListener vcl);

    /**
     * remove a vetoable change listener to this child
     */

    void removeVetoableChangeListener(String name, VetoableChangeListener vcl);

}
