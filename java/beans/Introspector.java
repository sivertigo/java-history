/*
 * @(#)Introspector.java	1.92 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.beans;

import java.lang.reflect.*;
import java.security.*;


/**
 * The Introspector class provides a standard way for tools to learn about
 * the properties, events, and methods supported by a target Java Bean.
 * <p>
 * For each of those three kinds of information, the Introspector will
 * separately analyze the bean's class and superclasses looking for
 * either explicit or implicit information and use that information to
 * build a BeanInfo object that comprehensively describes the target bean.
 * <p>
 * For each class "Foo", explicit information may be available if there exists
 * a corresponding "FooBeanInfo" class that provides a non-null value when
 * queried for the information.   We first look for the BeanInfo class by
 * taking the full package-qualified name of the target bean class and
 * appending "BeanInfo" to form a new class name.  If this fails, then
 * we take the final classname component of this name, and look for that
 * class in each of the packages specified in the BeanInfo package search
 * path.
 * <p>
 * Thus for a class such as "sun.xyz.OurButton" we would first look for a
 * BeanInfo class called "sun.xyz.OurButtonBeanInfo" and if that failed we'd
 * look in each package in the BeanInfo search path for an OurButtonBeanInfo
 * class.  With the default search path, this would mean looking for
 * "sun.beans.infos.OurButtonBeanInfo".
 * <p>
 * If a class provides explicit BeanInfo about itself then we add that to
 * the BeanInfo information we obtained from analyzing any derived classes,
 * but we regard the explicit information as being definitive for the current
 * class and its base classes, and do not proceed any further up the superclass
 * chain.
 * <p>
 * If we don't find explicit BeanInfo on a class, we use low-level
 * reflection to study the methods of the class and apply standard design
 * patterns to identify property accessors, event sources, or public
 * methods.  We then proceed to analyze the class's superclass and add
 * in the information from it (and possibly on up the superclass chain).
 */

public class Introspector {

    // Flags that can be used to control getBeanInfo:
    public final static int USE_ALL_BEANINFO           = 1;
    public final static int IGNORE_IMMEDIATE_BEANINFO  = 2;
    public final static int IGNORE_ALL_BEANINFO        = 3;

    //======================================================================
    // 				Public methods
    //======================================================================

    /**
     * Introspect on a Java bean and learn about all its properties, exposed
     * methods, and events.
     *
     * @param beanClass  The bean class to be analyzed.
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public static BeanInfo getBeanInfo(Class beanClass) throws IntrospectionException {
	GenericBeanInfo bi = (GenericBeanInfo)beanInfoCache.get(beanClass);
	if (bi == null) {
	    bi = (new Introspector(beanClass, null, USE_ALL_BEANINFO)).getBeanInfo();
	    beanInfoCache.put(beanClass, bi);
	}

	// Make an independent copy of the BeanInfo.
	return new GenericBeanInfo(bi);
    }

    /**
     * Introspect on a Java bean and learn about all its properties, exposed
     * methods, and events, subnject to some comtrol flags.
     *
     * @param beanClass  The bean class to be analyzed.
     * @param flags  Flags to control the introspection.
     *     If flags == USE_ALL_BEANINFO then we use all of the BeanInfo
     *	 	classes we can discover.
     *     If flags == IGNORE_IMMEDIATE_BEANINFO then we ignore any
     *           BeanInfo associated with the specified beanClass.
     *     If flags == IGNORE_ALL_BEANINFO then we ignore all BeanInfo
     *           associated with the specified beanClass or any of its
     *		 parent classes.
     * @return  A BeanInfo object describing the target bean.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public static BeanInfo getBeanInfo(Class beanClass, int flags)
						throws IntrospectionException {
	// We don't use the cache.
	GenericBeanInfo bi = (new Introspector(beanClass, null, flags)).getBeanInfo();

	// Make an independent copy of the BeanInfo.
	return new GenericBeanInfo(bi);
    }

    /**
     * Introspect on a Java bean and learn all about its properties, exposed
     * methods, below a given "stop" point.
     *
     * @param bean The bean class to be analyzed.
     * @param stopClass The baseclass at which to stop the analysis.  Any
     *    methods/properties/events in the stopClass or in its baseclasses
     *    will be ignored in the analysis.
     * @exception IntrospectionException if an exception occurs during
     *              introspection.
     */
    public static BeanInfo getBeanInfo(Class beanClass,	Class stopClass)
						throws IntrospectionException {
	GenericBeanInfo bi = (new Introspector(beanClass, stopClass, USE_ALL_BEANINFO)).getBeanInfo();

	// Make an independent copy of the BeanInfo.
	return new GenericBeanInfo(bi);
    }

    /**
     * Utility method to take a string and convert it to normal Java variable
     * name capitalization.  This normally means converting the first
     * character from upper case to lower case, but in the (unusual) special
     * case when there is more than one character and both the first and
     * second characters are upper case, we leave it alone.
     * <p>
     * Thus "FooBah" becomes "fooBah" and "X" becomes "x", but "URL" stays
     * as "URL".
     *
     * @param  name The string to be decapitalized.
     * @return  The decapitalized version of the string.
     */
    public static String decapitalize(String name) {
	if (name == null || name.length() == 0) {
	    return name;
	}
	if (name.length() > 1 && Character.isUpperCase(name.charAt(1)) &&
			Character.isUpperCase(name.charAt(0))){
	    return name;
	}
	char chars[] = name.toCharArray();
	chars[0] = Character.toLowerCase(chars[0]);
	return new String(chars);
    }

    /**
     * Gets the list of package names that will be used for
     *		finding BeanInfo classes.
     *
     * @return  The array of package names that will be searched in
     *		order to find BeanInfo classes.
     * <p>     This is initially set to {"sun.beans.infos"}.
     */

    public static synchronized String[] getBeanInfoSearchPath() {
	// Return a copy of the searchPath.
	String result[] = new String[searchPath.length];
	for (int i = 0; i < searchPath.length; i++) {
	    result[i] = searchPath[i];
	}
	return result;
    }

    /**
     * Change the list of package names that will be used for
     *		finding BeanInfo classes.
     * 
     * <p>First, if there is a security manager, its <code>checkPropertiesAccess</code> 
     * method is called. This could result in a SecurityException.
     * 
     * @param path  Array of package names.
     * @exception  SecurityException  if a security manager exists and its  
     *             <code>checkPropertiesAccess</code> method doesn't allow setting
     *              of system properties.
     * @see SecurityManager#checkPropertiesAccess
     */

    public static synchronized void setBeanInfoSearchPath(String path[]) {
	SecurityManager sm = System.getSecurityManager();
	if (sm != null) {
	    sm.checkPropertiesAccess();
	}
	searchPath = path;
    }


    /**
     * Flush all of the Introspector's internal caches.  This method is
     * not normally required.  It is normally only needed by advanced
     * tools that update existing "Class" objects in-place and need
     * to make the Introspector re-analyze existing Class objects.
     */

    public static void flushCaches() {
	beanInfoCache.clear();
	declaredMethodCache.clear();
    }

    /**
     * Flush the Introspector's internal cached information for a given class.
     * This method is not normally required.  It is normally only needed
     * by advanced tools that update existing "Class" objects in-place
     * and need to make the Introspector re-analyze an existing Class object.
     *
     * Note that only the direct state associated with the target Class
     * object is flushed.  We do not flush state for other Class objects
     * with the same name, nor do we flush state for any related Class
     * objects (such as subclasses), even though their state may include
     * information indirectly obtained from the target Class object.
     *
     * @param clz  Class object to be flushed.
     */

    public static void flushFromCaches(Class clz) {
	beanInfoCache.remove(clz);
	declaredMethodCache.remove(clz);
    }

    //======================================================================
    // 			Private implementation methods
    //======================================================================

    private Introspector(Class beanClass, Class stopClass, int flags)
					    throws IntrospectionException {
	this.beanClass = beanClass;

	// Check stopClass is a superClass of startClass.
	if (stopClass != null) {
	    boolean isSuper = false;
	    for (Class c = beanClass.getSuperclass(); c != null; c = c.getSuperclass()) {
	        if (c == stopClass) {
		    isSuper = true;
	        }
	    }
	    if (!isSuper) {
	        throw new IntrospectionException(stopClass.getName() + " not superclass of " + 
					beanClass.getName());
	    }
	}

        if (flags == USE_ALL_BEANINFO) {
	    informant = findInformant(beanClass);
        }

	Class superClass = beanClass.getSuperclass();
	if (superClass != stopClass) {
	    int newFlags = flags;
	    if (newFlags == IGNORE_IMMEDIATE_BEANINFO) {
		newFlags = USE_ALL_BEANINFO;
	    }
	    if (stopClass == null && newFlags == USE_ALL_BEANINFO) {
		// We avoid going through getBeanInfo as we don't need
		// it do copy the BeanInfo.
		superBeanInfo = (BeanInfo)beanInfoCache.get(superClass);
		if (superBeanInfo == null) {
		    Introspector ins = new Introspector(superClass, null, USE_ALL_BEANINFO);
	    	    superBeanInfo = ins.getBeanInfo();
	    	    beanInfoCache.put(superClass, superBeanInfo);
		}
	    } else {
		Introspector ins = new Introspector(superClass, stopClass, newFlags);
	        superBeanInfo = ins.getBeanInfo();
	    }
	}
	if (informant != null) {
	    additionalBeanInfo = informant.getAdditionalBeanInfo();
	} 
	if (additionalBeanInfo == null) {
	    additionalBeanInfo = new BeanInfo[0];
	}
    }

   
    private GenericBeanInfo getBeanInfo() throws IntrospectionException {

	// the evaluation order here is import, as we evaluate the
	// event sets and locate PropertyChangeListeners before we
	// look for properties.
	BeanDescriptor bd = getTargetBeanDescriptor();
	EventSetDescriptor esds[] = getTargetEventInfo();
	int defaultEvent = getTargetDefaultEventIndex();
	PropertyDescriptor pds[] = getTargetPropertyInfo();
	int defaultProperty = getTargetDefaultPropertyIndex();
	MethodDescriptor mds[] = getTargetMethodInfo();

        return new GenericBeanInfo(bd, esds, defaultEvent, pds,
			defaultProperty, mds, informant);
	
    }

    private static synchronized BeanInfo findInformant(Class beanClass) {
	String name = beanClass.getName() + "BeanInfo";
        try {
	    return (java.beans.BeanInfo)instantiate(beanClass, name);
	} catch (Exception ex) {
	    // Just drop through
        }
	// Now try checking if the bean is its own BeanInfo.
        try {
	    if (isSubclass(beanClass, java.beans.BeanInfo.class)) {
	        return (java.beans.BeanInfo)beanClass.newInstance();
	    }
	} catch (Exception ex) {
	    // Just drop through
        }
	// Now try looking for <searchPath>.fooBeanInfo
   	while (name.indexOf('.') > 0) {
	    name = name.substring(name.indexOf('.')+1);
	}
	for (int i = 0; i < searchPath.length; i++) {
	    try {
		String fullName = searchPath[i] + "." + name;
	        return (java.beans.BeanInfo)instantiate(beanClass, fullName);
	    } catch (Exception ex) {
	       // Silently ignore any errors.
	    }
	}
	return null;
    }

    /**
     * @return An array of PropertyDescriptors describing the editable
     * properties supported by the target bean.
     */

    private PropertyDescriptor[] getTargetPropertyInfo() throws IntrospectionException {

	// Check if the bean has its own BeanInfo that will provide
	// explicit information.
        PropertyDescriptor[] explicit = null;
	if (informant != null) {
	    explicit = informant.getPropertyDescriptors();
	    int ix = informant.getDefaultPropertyIndex();
	    if (ix >= 0 && ix < explicit.length) {
		defaultPropertyName = explicit[ix].getName();
	    }
        }

	if (explicit == null && superBeanInfo != null) {
	    // We have no explicit BeanInfo properties.  Check with our parent.
	    PropertyDescriptor supers[] = superBeanInfo.getPropertyDescriptors();
	    for (int i = 0 ; i < supers.length; i++) {
		addProperty(supers[i]);
	    }
	    int ix = superBeanInfo.getDefaultPropertyIndex();
	    if (ix >= 0 && ix < supers.length) {
		defaultPropertyName = supers[ix].getName();
	    }
	}

	for (int i = 0; i < additionalBeanInfo.length; i++) {
	    PropertyDescriptor additional[] = additionalBeanInfo[i].getPropertyDescriptors();
	    if (additional != null) {
	        for (int j = 0 ; j < additional.length; j++) {
		    addProperty(additional[j]);
	        }
	    }
	}

	if (explicit != null) {
	    // Add the explicit informant data to our results.
	    for (int i = 0 ; i < explicit.length; i++) {
		addProperty(explicit[i]);
	    }

	} else {

	    // Apply some reflection to the current class.

	    // First get an array of all the public methods at this level
	    Method methodList[] = getPublicDeclaredMethods(beanClass);

	    // Now analyze each method.
	    for (int i = 0; i < methodList.length; i++) {
	        Method method = methodList[i];
		if (method == null) {
		    continue;
		}
	        // skip static methods.
		int mods = method.getModifiers();
		if (Modifier.isStatic(mods)) {
		    continue;
		}
	        String name = method.getName();
	        Class argTypes[] = method.getParameterTypes();
	        Class resultType = method.getReturnType();
		int argCount = argTypes.length;
		PropertyDescriptor pd = null;

		try {

	            if (argCount == 0) {
		        if (name.startsWith("get")) {
		            // Simple getter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(3)),
						method, null);
	                } else if (resultType == boolean.class && name.startsWith("is")) {
		            // Boolean getter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(2)),
						method, null);
		        }
	            } else if (argCount == 1) {
		        if (argTypes[0] == int.class && name.startsWith("get")) {
		            pd = new IndexedPropertyDescriptor(
					decapitalize(name.substring(3)),
					null, null,
					method,	null);
		        } else if (resultType == void.class && name.startsWith("set")) {
		            // Simple setter
	                    pd = new PropertyDescriptor(decapitalize(name.substring(3)),
						null, method);
		            if (throwsException(method, PropertyVetoException.class)) {
			        pd.setConstrained(true);
			    }			
		        }
	            } else if (argCount == 2) {
			    if (argTypes[0] == int.class && name.startsWith("set")) {
	                    pd = new IndexedPropertyDescriptor(
						decapitalize(name.substring(3)),
						null, null,
						null, method);
		            if (throwsException(method, PropertyVetoException.class)) {
			        pd.setConstrained(true);			
			    }
			}
		    }
		} catch (IntrospectionException ex) {
		    // This happens if a PropertyDescriptor or IndexedPropertyDescriptor
	            // constructor fins that the method violates details of the deisgn
		    // pattern, e.g. by having an empty name, or a getter returning
		    // void , or whatever.
		    pd = null;
		}

		if (pd != null) {
		    // If this class or one of its base classes is a PropertyChange
		    // source, then we assume that any properties we discover are "bound".
		    if (propertyChangeSource) {
			pd.setBound(true);
		    }
		    addProperty(pd);
		}
	    }
	}

	// Allocate and populate the result array.
	PropertyDescriptor result[] = new PropertyDescriptor[properties.size()];
	java.util.Enumeration elements = properties.elements();
	for (int i = 0; i < result.length; i++) {
	    result[i] = (PropertyDescriptor)elements.nextElement();
	    if (defaultPropertyName != null
			 && defaultPropertyName.equals(result[i].getName())) {
		defaultPropertyIndex = i;
	    }
	}

	return result;
    }

    void addProperty(PropertyDescriptor pd) {
	String name = pd.getName();
	PropertyDescriptor old = (PropertyDescriptor)properties.get(name);
	if (old == null) {
	    properties.put(name, pd);
	    return;
	}
	// If the property type has changed, use the new descriptor.
	Class opd = old.getPropertyType();
	Class npd = pd.getPropertyType();
	if (opd != null && npd != null && opd != npd) {
	    properties.put(name, pd);
	    return;
	}

	PropertyDescriptor composite;
	if (old instanceof IndexedPropertyDescriptor ||
				pd instanceof IndexedPropertyDescriptor) {
	    composite = new IndexedPropertyDescriptor(old, pd);
	} else {
	    composite = new PropertyDescriptor(old, pd);
	}
	properties.put(name, composite);
    }


    /**
     * @return An array of EventSetDescriptors describing the kinds of 
     * events fired by the target bean.
     */
    private EventSetDescriptor[] getTargetEventInfo() throws IntrospectionException {

	// Check if the bean has its own BeanInfo that will provide
	// explicit information.
        EventSetDescriptor[] explicit = null;
	if (informant != null) {
	    explicit = informant.getEventSetDescriptors();
	    int ix = informant.getDefaultEventIndex();
	    if (ix >= 0 && ix < explicit.length) {
		defaultEventName = explicit[ix].getName();
	    }
	}

	if (explicit == null && superBeanInfo != null) {
	    // We have no explicit BeanInfo events.  Check with our parent.
	    EventSetDescriptor supers[] = superBeanInfo.getEventSetDescriptors();
	    for (int i = 0 ; i < supers.length; i++) {
		addEvent(supers[i]);
	    }
	    int ix = superBeanInfo.getDefaultEventIndex();
	    if (ix >= 0 && ix < supers.length) {
		defaultEventName = supers[ix].getName();
	    }
	}

	for (int i = 0; i < additionalBeanInfo.length; i++) {
	    EventSetDescriptor additional[] = additionalBeanInfo[i].getEventSetDescriptors();
	    if (additional != null) {
	        for (int j = 0 ; j < additional.length; j++) {
		    addEvent(additional[j]);
	        }
	    }
	}

	if (explicit != null) {
	    // Add the explicit informant data to our results.
	    for (int i = 0 ; i < explicit.length; i++) {
		addEvent(explicit[i]);
	    }

	} else {

	    // Apply some reflection to the current class.

	    // Get an array of all the public beans methods at this level
	    Method methodList[] = getPublicDeclaredMethods(beanClass);

	    // Find all suitable "add" and "remove" methods.
	    java.util.Hashtable adds = new java.util.Hashtable();
	    java.util.Hashtable removes = new java.util.Hashtable();
	    for (int i = 0; i < methodList.length; i++) {
	        Method method = methodList[i];
		if (method == null) {
		    continue;
		}
	        // skip static methods.
		int mods = method.getModifiers();
		if (Modifier.isStatic(mods)) {
		    continue;
		}
	        String name = method.getName();

	        Class argTypes[] = method.getParameterTypes();
	        Class resultType = method.getReturnType();

	        if (name.startsWith("add") && argTypes.length == 1 &&
			    	resultType == Void.TYPE) {
		    String compound = name.substring(3) + ":" + argTypes[0];
		    adds.put(compound, method);
	        } else if (name.startsWith("remove") && argTypes.length == 1 &&
			    	resultType == Void.TYPE) {
		    String compound = name.substring(6) + ":" + argTypes[0];
		    removes.put(compound, method);
	        }
	    }

   	    // Now look for matching addFooListener+removeFooListener pairs.
  	    java.util.Enumeration keys = adds.keys();
	    String beanClassName = beanClass.getName();
	    while (keys.hasMoreElements()) {
	        String compound = (String) keys.nextElement();
	        // Skip any "add" which doesn't have a matching "remove".
	        if (removes.get(compound) == null) {
		    continue;
	        } 
	        // Method name has to end in "Listener"
	        if (compound.indexOf("Listener:") <= 0) {
		    continue;
	        }

	        String listenerName = compound.substring(0, compound.indexOf(':'));
	        String eventName = decapitalize(listenerName.substring(0, listenerName.length()-8));
	        Method addMethod = (Method)adds.get(compound);
	        Method removeMethod = (Method)removes.get(compound);
	        Class argType = addMethod.getParameterTypes()[0];

	        // Check if the argument type is a subtype of EventListener
	        if (!Introspector.isSubclass(argType, eventListenerType)) {
	            continue;
	        }

                // generate a list of Method objects for each of the target methods:
	        Method allMethods[] = argType.getMethods();
	        int count = 0;
	        for (int i = 0; i < allMethods.length; i++) {
	            if (isEventHandler(allMethods[i])) {
		        count++;
	            } else {
		        allMethods[i] = null;
	            }
	        }
	        Method methods[] = new Method[count];
	        int j = 0;
	        for (int i = 0; i < allMethods.length; i++) {
	            if (allMethods[i] != null) {
		        methods[j++] = allMethods[i];
	            }
 	        }

  	        EventSetDescriptor esd = new EventSetDescriptor(eventName, argType,
						methods, addMethod, removeMethod);

		// If the adder method throws the TooManyListenersException then it
		// is a Unicast event source.
		if (throwsException(addMethod,
			java.util.TooManyListenersException.class)) {
		    esd.setUnicast(true);
		}

		addEvent(esd);
	    }
	}

	// Allocate and populate the result array.
	EventSetDescriptor result[] = new EventSetDescriptor[events.size()];
	java.util.Enumeration elements = events.elements();
	for (int i = 0; i < result.length; i++) {
	    result[i] = (EventSetDescriptor)elements.nextElement();
	    if (defaultEventName != null 
			    && defaultEventName.equals(result[i].getName())) {
		defaultEventIndex = i;
	    }
	}

	return result;
    }

    void addEvent(EventSetDescriptor esd) {
	String key = esd.getName() + esd.getListenerType();
	if (esd.getName().equals("propertyChange")) {
	    propertyChangeSource = true;
	}
	EventSetDescriptor old = (EventSetDescriptor)events.get(key);
	if (old == null) {
	    events.put(key, esd);
	    return;
	}
	EventSetDescriptor composite = new EventSetDescriptor(old, esd);
	events.put(key, composite);
    }

    /**
     * @return An array of MethodDescriptors describing the private
     * methods supported by the target bean.
     */
    private MethodDescriptor[] getTargetMethodInfo() throws IntrospectionException {

	// Check if the bean has its own BeanInfo that will provide
	// explicit information.
        MethodDescriptor[] explicit = null;
	if (informant != null) {
	    explicit = informant.getMethodDescriptors();
	}

	if (explicit == null && superBeanInfo != null) {
	    // We have no explicit BeanInfo methods.  Check with our parent.
	    MethodDescriptor supers[] = superBeanInfo.getMethodDescriptors();
	    for (int i = 0 ; i < supers.length; i++) {
		addMethod(supers[i]);
	    }
	}

	for (int i = 0; i < additionalBeanInfo.length; i++) {
	    MethodDescriptor additional[] = additionalBeanInfo[i].getMethodDescriptors();
	    if (additional != null) {
	        for (int j = 0 ; j < additional.length; j++) {
		    addMethod(additional[j]);
	        }
	    }
	}

	if (explicit != null) {
	    // Add the explicit informant data to our results.
	    for (int i = 0 ; i < explicit.length; i++) {
		addMethod(explicit[i]);
	    }

	} else {

	    // Apply some reflection to the current class.

	    // First get an array of all the beans methods at this level
	    Method methodList[] = getPublicDeclaredMethods(beanClass);

	    // Now analyze each method.
	    for (int i = 0; i < methodList.length; i++) {
	        Method method = methodList[i];
		if (method == null) {
		    continue;
		}
		MethodDescriptor md = new MethodDescriptor(method);
		addMethod(md);
	    }
	}

	// Allocate and populate the result array.
	MethodDescriptor result[] = new MethodDescriptor[methods.size()];
	java.util.Enumeration elements = methods.elements();
	for (int i = 0; i < result.length; i++) {
	    result[i] = (MethodDescriptor)elements.nextElement();
	}

	return result;
    }

    private void addMethod(MethodDescriptor md) {
	// We have to be careful here to distinguish method by both name
	// and argument lists.
	// This method gets called a *lot, so we try to be efficient.

	String name = md.getMethod().getName();

	MethodDescriptor old = (MethodDescriptor)methods.get(name);
	if (old == null) {
	    // This is the common case.
	    methods.put(name, md);
	    return;
	}	

	// We have a collision on method names.  This is rare.

	// Check if old and md have the same type.
	Class p1[] = md.getMethod().getParameterTypes();	
	Class p2[] = old.getMethod().getParameterTypes();	
	boolean match = false;
	if (p1.length == p2.length) {
	    match = true;
	    for (int i = 0; i < p1.length; i++) {
		if (p1[i] != p2[i]) {
		    match = false;
		    break;
		}
	    }
	}
	if (match) {
	    MethodDescriptor composite = new MethodDescriptor(old, md);
	    methods.put(name, composite);
	    return;
	}

	// We have a collision on method names with different type signatures.
	// This is very rare.

	String longKey = makeQualifiedMethodName(md);
	old = (MethodDescriptor)methods.get(longKey);
	if (old == null) {
	    methods.put(longKey, md);
	    return;
	}	
	MethodDescriptor composite = new MethodDescriptor(old, md);
	methods.put(longKey, composite);
    }

    private String makeQualifiedMethodName(MethodDescriptor md) {
	Method m = md.getMethod();
	StringBuffer sb = new StringBuffer();
	sb.append(m.getName());
	sb.append("=");
	Class params[] = m.getParameterTypes();
	for (int i = 0; i < params.length; i++) {
	    sb.append(":");
	    sb.append(params[i].getName());
	}
	return sb.toString();
    }

    private int getTargetDefaultEventIndex() {
	return defaultEventIndex;
    }

    private int getTargetDefaultPropertyIndex() {
	return defaultPropertyIndex;
    }

    private BeanDescriptor getTargetBeanDescriptor() throws IntrospectionException {
	// Use explicit info, if available,
	if (informant != null) {
	    BeanDescriptor bd = informant.getBeanDescriptor();
	    if (bd != null) {
		return (bd);
	    }
	}
	// OK, fabricate a default BeanDescriptor.
	return (new BeanDescriptor(beanClass));
    }

    private boolean isEventHandler(Method m) throws IntrospectionException {
	// We assume that a method is an event handler if it has a single
        // argument, whose type inherit from java.util.Event.

	try {
	    Class argTypes[] = m.getParameterTypes();
	    if (argTypes.length != 1) {
		return false;
	    }
	    if (isSubclass(argTypes[0], java.util.EventObject.class)) {
		return true;
	    } else {
		return false;
	    }
	} catch (Exception ex) {
	    throw new IntrospectionException("Unexpected reflection exception: " + ex);
	}
    }

    /*
     * Internal method to return *public* methods within a class.
     */

    private static synchronized Method[] getPublicDeclaredMethods(Class clz) {
	// Looking up Class.getDeclaredMethods is relatively expensive,
	// so we cache the results.
	final Class fclz = clz;
	Method[] result = (Method[])declaredMethodCache.get(fclz);
	if (result != null) {
	    return result;
	}

	// We have to raise privilege for getDeclaredMethods
	result = (Method[]) AccessController.doPrivileged(new PrivilegedAction() {
		public Object run() {
		    return fclz.getDeclaredMethods();
		}
	    });


	// Null out any non-public methods.
	for (int i = 0; i < result.length; i++) {
	    Method method = result[i];
	    int mods = method.getModifiers();
	    if (!Modifier.isPublic(mods)) {
	 	result[i] = null;
	    }
        }    
	// Add it to the cache.
	declaredMethodCache.put(clz, result);
	return result;
    }

    //======================================================================
    // Package private support methods.
    //======================================================================

    /**
     * Internal support for finding a target methodName on a given class.
     */
    private static Method internalFindMethod(Class start, String methodName,
								 int argCount) {

	// For overriden methods we need to find the most derived version.
	// So we start with the given class and walk up the superclass chain.
	for (Class cl = start; cl != null; cl = cl.getSuperclass()) {
            Method methods[] = getPublicDeclaredMethods(cl);
	    for (int i = 0; i < methods.length; i++) {
	        Method method = methods[i];
		if (method == null) {
		    continue;
		}
	        // skip static methods.
		int mods = method.getModifiers();
		if (Modifier.isStatic(mods)) {
		    continue;
		}
	        if (method.getName().equals(methodName) &&
			method.getParameterTypes().length == argCount) {
	            return method;
 	        }
	    }
	}

	// Now check any inherited interfaces.  This is necessary both when
	// the argument class is itself an interface, and when the argument
	// class is an abstract class.
	Class ifcs[] = start.getInterfaces();
	for (int i = 0 ; i < ifcs.length; i++) {
	    Method m = internalFindMethod(ifcs[i], methodName, argCount);
	    if (m != null) {
		return m;
	    }
	}

	return null;
    }

    /**
     * Find a target methodName on a given class.
     */
    static Method findMethod(Class cls, String methodName, int argCount) 
			throws IntrospectionException {
	if (methodName == null) {
	    return null;
	}

	Method m = internalFindMethod(cls, methodName, argCount);
	if (m != null ) {
	    return m;
	}

	// We failed to find a suitable method
	throw new IntrospectionException("No method \"" + methodName + 
					"\" with " + argCount + " arg(s)");
    }

    /**
     * Return true if class a is either equivalent to class b, or
     * if class a is a subclass of class b, i.e. if a either "extends"
     * or "implements" b.
     * Note tht either or both "Class" objects may represent interfaces.
     */
    static  boolean isSubclass(Class a, Class b) {
	// We rely on the fact that for any given java class or
        // primtitive type there is a unqiue Class object, so
	// we can use object equivalence in the comparisons.
	if (a == b) {
	    return true;
	}
	if (a == null || b == null) {
	    return false;
	}
	for (Class x = a; x != null; x = x.getSuperclass()) {
	    if (x == b) {	
		return true;
	    }
	    if (b.isInterface()) {
		Class interfaces[] = x.getInterfaces();
		for (int i = 0; i < interfaces.length; i++) {
		    if (isSubclass(interfaces[i], b)) {
			return true;
		    }
		}
	    }
	}
	return false;
    }

    /**
     * Return true iff the given method throws the given exception.
     */
    private boolean throwsException(Method method, Class exception) {
	Class exs[] = method.getExceptionTypes();
	for (int i = 0; i < exs.length; i++) {
	    if (exs[i] == exception) {
		return true;
	    }
	}
	return false;
    }

    /**
     * Try to create an instance of a named class.
     * First try the classloader of "sibling", then try the system
     * classloader.
     */

    static Object instantiate(Class sibling, String className)
		 throws InstantiationException, IllegalAccessException,
						ClassNotFoundException {
	// First check with sibling's classloader (if any). 
	ClassLoader cl = sibling.getClassLoader();
	if (cl != null) {
	    try {
	        Class cls = cl.loadClass(className);
		return cls.newInstance();
	    } catch (Exception ex) {
	        // Just drop through and try the system classloader.
	    }
        }
	// Now try the system classloader.
	try {
	    cl = ClassLoader.getSystemClassLoader();
	    if (cl != null) {
	        Class cls = cl.loadClass(className);
		return cls.newInstance();
	    }
        } catch (Exception ex) {
	    // We're not allowed to access the system class loader or
	    // the class creation failed.
	    // Drop through.
	}

	// Now try the bootstrap classloader.
	Class cls = Class.forName(className);
	return cls.newInstance();
    }

    //======================================================================

    private BeanInfo informant;
    private boolean propertyChangeSource = false;
    private Class beanClass;
    private BeanInfo superBeanInfo;
    private BeanInfo additionalBeanInfo[];
    private static java.util.Hashtable beanInfoCache = new java.util.Hashtable();
    private static Class eventListenerType = java.util.EventListener.class;
    private String defaultEventName;
    private String defaultPropertyName;
    private int defaultEventIndex = -1;
    private int defaultPropertyIndex = -1;

    // Methods maps from Method objects to MethodDescriptors
    private java.util.Hashtable methods = new java.util.Hashtable();

    // Cache of Class.getDeclaredMethods:
    private static java.util.Hashtable declaredMethodCache = new java.util.Hashtable();

    // properties maps from String names to PropertyDescriptors
    private java.util.Hashtable properties = new java.util.Hashtable();

    // events maps from String names to EventSetDescriptors
    private java.util.Hashtable events = new java.util.Hashtable();

    private static String[] searchPath = { "sun.beans.infos" };

}

//===========================================================================

/**
 * Package private implementation support class for Introspector's
 * internal use.
 */

class GenericBeanInfo extends SimpleBeanInfo {

    public GenericBeanInfo(BeanDescriptor beanDescriptor,
		EventSetDescriptor[] events, int defaultEvent,
		PropertyDescriptor[] properties, int defaultProperty,
		MethodDescriptor[] methods, BeanInfo targetBeanInfo) {
	this.beanDescriptor = beanDescriptor;
	this.events = events;
	this.defaultEvent = defaultEvent;
	this.properties = properties;
	this.defaultProperty = defaultProperty;
	this.methods = methods;
	this.targetBeanInfo = targetBeanInfo;
    }

    /**
     * Package-private dup constructor
     * This must isolate the new object from any changes to the old object.
     */
    GenericBeanInfo(GenericBeanInfo old) {

	beanDescriptor = new BeanDescriptor(old.beanDescriptor);
	if (old.events != null) {
	    int len = old.events.length;
	    events = new EventSetDescriptor[len];
	    for (int i = 0; i < len; i++) {
		events[i] = new EventSetDescriptor(old.events[i]);
	    }
	}
	defaultEvent = old.defaultEvent;
	if (old.properties != null) {
	    int len = old.properties.length;
	    properties = new PropertyDescriptor[len];
	    for (int i = 0; i < len; i++) {
		PropertyDescriptor oldp = old.properties[i];
		if (oldp instanceof IndexedPropertyDescriptor) {
		    properties[i] = new IndexedPropertyDescriptor(
					(IndexedPropertyDescriptor) oldp);
		} else {
		    properties[i] = new PropertyDescriptor(oldp);
		}
	    }
	}
	defaultProperty = old.defaultProperty;
	if (old.methods != null) {
	    int len = old.methods.length;
	    methods = new MethodDescriptor[len];
	    for (int i = 0; i < len; i++) {
		methods[i] = new MethodDescriptor(old.methods[i]);
	    }
	}
	targetBeanInfo = old.targetBeanInfo;
    }

    public PropertyDescriptor[] getPropertyDescriptors() {
	return properties;
    }

    public int getDefaultPropertyIndex() {
	return defaultProperty;
    }

    public EventSetDescriptor[] getEventSetDescriptors() {
	return events;
    }

    public int getDefaultEventIndex() {
	return defaultEvent;
    }

    public MethodDescriptor[] getMethodDescriptors() {
	return methods;
    }

    public BeanDescriptor getBeanDescriptor() {
	return beanDescriptor;
    }

    public java.awt.Image getIcon(int iconKind) {
	if (targetBeanInfo != null) {
	    return targetBeanInfo.getIcon(iconKind);
	}
	return super.getIcon(iconKind);
    }

    private BeanDescriptor beanDescriptor;
    private EventSetDescriptor[] events;
    private int defaultEvent;
    private PropertyDescriptor[] properties;
    private int defaultProperty;
    private MethodDescriptor[] methods;
    private BeanInfo targetBeanInfo;
}
