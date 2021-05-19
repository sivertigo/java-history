/*
 * @(#)AttributeSet.java	1.27 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.text;

import java.util.Enumeration;

/**
 * A collection of unique attributes.  This is a read-only, 
 * immutable interface.  An attribute is basically a key and
 * a value assigned to the key.  The collection may represent
 * something like a style run, a logical style, etc.  These
 * are generally used to describe features that will contribute
 * to some graphical representation such as a font.  The
 * set of possible keys is unbounded and can be anything.
 * Typically View implementations will respond to attribute
 * definitions and render something to represent the attributes.
 * <p>
 * Attributes can potentially resolve in a hierarchy.  If a 
 * key doesn't resolve locally, and a resolving parent
 * exists, the key will be resolved through the parent.
 *
 * @author  Timothy Prinzing
 * @version 1.27 11/29/01
 * @see MutableAttributeSet
 * @see AttributeCharacterIterator
 */
public interface AttributeSet {

    /**
     * This interface is the type signature that is expected
     * to be present on any attribute key that contributes to
     * the determination of what font to use to render some 
     * text.  This is not considered to be a closed set, the 
     * definition can change across version of the JDK and can 
     * be ammended by additional user added entries that 
     * correspond to logical settings that are specific to
     * some type of content.
     */
    public interface FontAttribute {
    }

    /**
     * This interface is the type signature that is expected
     * to be present on any attribute key that contributes to
     * presentation of color.
     */
    public interface ColorAttribute {
    }

    /**
     * This interface is the type signature that is expected
     * to be present on any attribute key that contributes to
     * character level presentation.  This would be any attribute
     * that applies to a so-called <term>run</term> of 
     * style.
     */
    public interface CharacterAttribute {
    }

    /**
     * This interface is the type signature that is expected
     * to be present on any attribute key that contributes to
     * the paragraph level presentation.
     */
    public interface ParagraphAttribute {
    }

    /**
     * Returns the number of attributes contained in this set.
     *
     * @return the number of attributes >= 0
     */
    public int getAttributeCount();

    /**
     * Checks whether the named attribute has a value specified in
     * the set without resolving through another attribute
     * set.
     *
     * @param attrName the attribute name
     * @return true if the attribute has a value specified
     */
    public boolean isDefined(Object attrName);

    /**
     * Determines if the two attribute sets are equivalent.
     *
     * @param attr an attribute set
     * @return true if the sets are equivalent
     */
    public boolean isEqual(AttributeSet attr);

    /**
     * Returns an attribute set that is guaranteed not
     * to change over time.  
     *
     * @return a copy of the attribute set
     */
    public AttributeSet copyAttributes();

    /**
     * Fetches the value of the given attribute. If the value is not found
     * locally, the search is continued upward through the resolving 
     * parent (if one exists) until the value is either
     * found or there are no more parents.  If the value is not found,
     * null is returned.
     *
     * @param key the non-null key of the attribute binding
     * @return the value
     */
    public Object getAttribute(Object key);

    /**
     * Returns an enumeration over the names of the attributes in the set.
     * The elements of the enumeration are all Strings.  The set does
     * not include the resolving parent, if one is defined.
     *
     * @return the names
     */
    public Enumeration getAttributeNames();

    /**
     * Returns true if this set contains this attribute with an equal value.
     *
     * @param name the non-null attribute name
     * @param value the value
     * @return true if the set contains the attribute with an equal value
     */
    public boolean containsAttribute(Object name, Object value);

    /**
     * Returns true if this set contains all the attributes with equal values.
     *
     * @param attributes the set of attributes to check against
     * @return true if this set contains all the attributes with equal values
     */
    public boolean containsAttributes(AttributeSet attributes);

    /**
     * Gets the resolving parent.
     *
     * @return the parent
     */
    public AttributeSet getResolveParent();

    /**
     * Attribute name used to name the collection of
     * attributes.
     */
    public static final Object NameAttribute = StyleConstants.NameAttribute;

    /**
     * Attribute name used to identifiy the resolving parent
     * set of attributes, if one is defined.
     */
    public static final Object ResolveAttribute = StyleConstants.ResolveAttribute;

}