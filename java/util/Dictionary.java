/*
 * @(#)Dictionary.java	1.9 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.util;

/**
 * The <code>Dictionary</code> class is the abstract parent of any 
 * class, such as <code>Hashtable</code>, which maps keys to values. 
 * Any non-<code>null</code> object can be used as a key and as a value.
 * <p>
 * As a rule, the <code>equals</code> method should be used by 
 * implementations of this class to decide if two keys are the same. 
 *
 * @author  unascribed
 * @version 1.9, 12/10/01
 * @see     java.lang.Object#equals(java.lang.Object)
 * @see     java.lang.Object#hashCode()
 * @see     java.util.Hashtable
 * @since   JDK1.0
 */
public abstract
class Dictionary {
    /**
     * Returns the number of keys in this dictionary.
     *
     * @return  the number of keys in this dictionary.
     * @since   JDK1.0
     */
    abstract public int size();

    /**
     * Tests if this dictionary maps no keys to value.
     *
     * @return  <code>true</code> if this dictionary maps no keys to values;
     *          <code>false</code> otherwise.
     * @since   JDK1.0
     */
    abstract public boolean isEmpty();

    /**
     * Returns an enumeration of the keys in this dictionary.
     *
     * @return  an enumeration of the keys in this dictionary.
     * @see     java.util.Dictionary#elements()
     * @see     java.util.Enumeration
     * @since   JDK1.0
     */
    abstract public Enumeration keys();

    /**
     * Returns an enumeration of the values in this dictionary.
     * the Enumeration methods on the returned object to fetch the elements
     * sequentially.
     *
     * @return  an enumeration of the values in this dictionary.
     * @see     java.util.Dictionary#keys()
     * @see     java.util.Enumeration
     * @since   JDK1.0
     */
    abstract public Enumeration elements();

    /**
     * Returns the value to which the key is mapped in this dictionary.
     *
     * @return  the value to which the key is mapped in this dictionary;
     * @param   key   a key in this dictionary.
     *          <code>null</code> if the key is not mapped to any value in
     *          this dictionary.
     * @see     java.util.Dictionary#put(java.lang.Object, java.lang.Object)
     * @since   JDK1.0
     */
    abstract public Object get(Object key);

    /**
     * Maps the specified <code>key</code> to the specified 
     * <code>value</code> in this dictionary. Neither the key nor the 
     * value can be <code>null</code>.
     * <p>
     * The <code>value</code> can be retrieved by calling the 
     * <code>get</code> method with a <code>key</code> that is equal to 
     * the original <code>key</code>. 
     *
     * @param      key     the hashtable key.
     * @param      value   the value.
     * @return     the previous value to which the <code>key</code> was mapped
     *             in this dictionary, or <code>null</code> if the key did not
     *             have a previous mapping.
     * @exception  NullPointerException  if the <code>key</code> or
     *               <code>value</code> is <code>null</code>.
     * @see        java.lang.Object#equals(java.lang.Object)
     * @see        java.util.Dictionary#get(java.lang.Object)
     * @since      JDK1.0
     */
    abstract public Object put(Object key, Object value);

    /**
     * Removes the <code>key</code> (and its corresponding 
     * <code>value</code>) from this dictionary. This method does nothing 
     * if the <code>key</code> is not in this dictionary. 
     *
     * @param   key   the key that needs to be removed.
     * @return  the value to which the <code>key</code> had been mapped in this
     *          dictionary, or <code>null</code> if the key did not have a
     *          mapping.
     * @since   JDK1.0
     */
    abstract public Object remove(Object key);
}
