/*
 * @(#)ComboBoxUI.java	1.14 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf;

import javax.swing.JComboBox;

/**
 * Pluggable look and feel interface for JComboBox.
 *
 * @version 1.14 11/29/01
 * @author Arnaud Weber
 * @author Tom Santos
 */
public abstract class ComboBoxUI extends ComponentUI {

    /**
     * Set the visiblity of the popup
     */
    public abstract void setPopupVisible( JComboBox c, boolean v );

    /** 
     * Determine the visibility of the popup
     */
    public abstract boolean isPopupVisible( JComboBox c );

    /** 
     * Determine whether or not the combo box itself is traversable 
     */
    public abstract boolean isFocusTraversable( JComboBox c );
}
