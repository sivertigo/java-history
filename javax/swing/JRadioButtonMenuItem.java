/*
 * @(#)JRadioButtonMenuItem.java	1.34 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing;

import java.util.EventListener;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.io.IOException;

import javax.swing.plaf.*;
import javax.accessibility.*;

/**
 * An implementation of a RadioButtonMenuItem. A RadioButtonMenuItem is
 * a menu item that is part of a group of menu items in which only one
 * item in the group can be selected. The selected item displays its
 * selected state. Selecting it causes any other selected item to
 * switch to the unselected state.
 * <p>
 * Used with a {@link ButtonGroup} object to create a group of menu items
 * in which only one item at a time can be selected. (Create a ButtonGroup
 * object and use its <code>add</code> method to include the JRadioButtonMenuItem
 * objects in the group.)
 * <p>
 * For the keyboard keys used by this component in the standard Look and
 * Feel (L&F) renditions, see the
 * <a href="doc-files/Key-Index.html#JRadioButtonMenuItem">JRadioButtonMenuItem</a> key assignments.
 * <p>
 * <strong>Warning:</strong>
 * Serialized objects of this class will not be compatible with
 * future Swing releases.  The current serialization support is appropriate
 * for short term storage or RMI between applications running the same
 * version of Swing.  A future release of Swing will provide support for
 * long term persistence.
 *
 * @beaninfo
 *   attribute: isContainer false
 *
 * @version 1.34 11/29/01
 * @author Georges Saab
 * @author David Karlton
 * @see ButtonGroup
 */
public class JRadioButtonMenuItem extends JMenuItem implements Accessible {
    /**
     * @see #getUIClassID
     * @see #readObject
     */
    private static final String uiClassID = "RadioButtonMenuItemUI";

    /**
     * Creates a JRadioButtonMenuItem with no set text or icon.
     */
    public JRadioButtonMenuItem() {
        this(null, null, false);
    }

    /**
     * Creates a JRadioButtonMenuItem with an icon.
     *
     * @param icon the Icon to display on the RadioButtonMenuItem.
     */
    public JRadioButtonMenuItem(Icon icon) {
        this(null, icon, false);
    }

    /**
     * Creates a JRadioButtonMenuItem with text.
     *
     * @param text the text of the RadioButtonMenuItem.
     */
    public JRadioButtonMenuItem(String text) {
        this(text, null, false);
    }
    
    /**
     * Creates a JRadioButtonMenuItem with the specified text
     * and Icon.
     *
     * @param text the text of the RadioButtonMenuItem
     * @param icon the icon to display on the RadioButtonMenuItem
     */
    public JRadioButtonMenuItem(String text, Icon icon) {
	this(text, icon, false);
    }

    /**
     * Creates a radiobutton menu item with the specified text 
     * and selection state.
     *
     * @param text the text of the CheckBoxMenuItem.
     * @param b the selected state of the checkboxmenuitem
     */
    public JRadioButtonMenuItem(String text, boolean b) {
        this(text);
        setSelected(b);
    }

    /**
     * Creates a radio button menu item with the specified image
     * and selection state, but no text.
     *   
     * @param icon  the image that the button should display
     * @param selected  if true, the button is initially selected;
     *                  otherwise, the button is initially unselected
     */
    public JRadioButtonMenuItem(Icon icon, boolean selected) {
        this(null, icon, selected);
    }

    /**
     * Creates a radio button menu item that has the specified 
     * text, image, and selection state.
     *
     * @param text  the string displayed on the radio button 
     * @param icon  the image that the button should display
     */
    public JRadioButtonMenuItem(String text, Icon icon, boolean selected) {
	super(text, icon);
        setModel(new JToggleButton.ToggleButtonModel());
        setSelected(selected);
    }

    /**
     * Returns the name of the L&F class that renders this component.
     *
     * @return "RadioButtonMenuItemUI"
     * @see JComponent#getUIClassID
     * @see UIDefaults#getUI
     */
    public String getUIClassID() {
        return uiClassID;
    }


    /**
     * Override Component.requestFocus() to not grab focus.
     */
    public void requestFocus() {}


    /** 
     * See readObject() and writeObject() in JComponent for more 
     * information about serialization in Swing.
     */
    private void writeObject(ObjectOutputStream s) throws IOException {
        s.defaultWriteObject();
	if ((ui != null) && (getUIClassID().equals(uiClassID))) {
	    ui.installUI(this);
	}
    }


    /**
     * Returns a string representation of this JRadioButtonMenuItem.
     * This method 
     * is intended to be used only for debugging purposes, and the 
     * content and format of the returned string may vary between      
     * implementations. The returned string may be empty but may not 
     * be <code>null</code>.
     * 
     * @return  a string representation of this JRadioButtonMenuItem.
     */
    protected String paramString() {
	return super.paramString();
    }

/////////////////                                                 
// Accessibility support
////////////////

    /**
     * Get the AccessibleContext associated with this JComponent
     *
     * @return the AccessibleContext of this JComponent
     */
    public AccessibleContext getAccessibleContext() {
        if (accessibleContext == null) {
            accessibleContext = new AccessibleJRadioButtonMenuItem();
        }
        return accessibleContext;
    }

    /**
     * The class used to obtain the accessible role for this object.
     * <p>
     * <strong>Warning:</strong>
     * Serialized objects of this class will not be compatible with
     * future Swing releases.  The current serialization support is appropriate
     * for short term storage or RMI between applications running the same
     * version of Swing.  A future release of Swing will provide support for
     * long term persistence.
     */
    protected class AccessibleJRadioButtonMenuItem extends AccessibleJMenuItem {
        /**
         * Get the role of this object.
         *
         * @return an instance of AccessibleRole describing the role of the 
         * object
         */
        public AccessibleRole getAccessibleRole() {
            return AccessibleRole.RADIO_BUTTON;
        }
    } // inner class AccessibleJRadioButtonMenuItem
}

