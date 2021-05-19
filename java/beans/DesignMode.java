/*
 * @(#)DesignMode.java	1.7 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

/**
 * <p>
 * This interface is intended to be implemented by, or delegated from, instances
 * of java.beans.BeanContext, in order to propagate to its nested hierarchy of
 * java.beans.BeanContextChild instances, the current "designTime" property.
 * </p>
 *
 * <p>
 * The JavaBeans specification defines the notion of design time as is a 
 * mode in which JavaBeans instances should function during their composition
 * and customization in a interactive design, composition or construction tool,
 * as opposed to runtime when the JavaBean is part of an applet, application,
 * or other live Java executable abstraction.
 * </p>
 *
 * @author Laurence P. G. Cable
 * @version 1.7
 * @since JDK1.2
 *
 * @see java.beans.BeanContext
 * @see java.beans.BeanContextChild
 * @see java.beans.BeanContextListener
 * @see java.beans.PropertyChangeEvent
 */

public interface DesignMode {

    /**
     * <p>
     * the standard value of the propertyName as fired from a BeanContext or
     * other source of PropertyChangeEvents.
     * </p>
     */

    static String PROPERTYNAME = "designTime";

    /**
     * Sets the "value" of the "designTime" property.
     *
     * @param designTime sets the current "value" of the "designTime" property.
     * <p>
     * If the implementing object is an instance of java.beans.BeanContext, or
     * a subinterface thereof, then that BeanContext should fire a
     * PropertyChangeEvent, to its registered BeanContextListeners, with
     * parameters:
     *
     * @param	propertyName	java.beans.DesignMode.PROPERTYNAME
     * @param   oldValue	previous value of "designTime"
     * @param   newValue	current value of "designTime"
     * </p>
     *
     * <p>
     * Note it is illegal for a BeanContextChild to invoke this method
     * associated with a BeanContext that it is nested within.
     * </p>
     *
     * @see java.beans.BeanContext
     * @see java.beans.BeanContextListener
     * @see java.beans.PropertyChangeEvent
     */

    void setDesignTime(boolean designTime);

    /**
     * <p>
     * A value of true denotes that JavaBeans should behave in design time
     * mode, a value of false denotes runtime behavior.
     * </p>
     *
     * @return the current "value" of the "designTime" property.
     */

    boolean isDesignTime();
}
