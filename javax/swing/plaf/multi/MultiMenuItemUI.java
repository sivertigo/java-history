/*
 * @(#)MultiMenuItemUI.java	1.23 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package javax.swing.plaf.multi;

import java.util.Vector;
import javax.swing.plaf.MenuItemUI;
import javax.swing.plaf.ButtonUI;
import javax.swing.plaf.ComponentUI;
import javax.swing.JComponent;
import java.awt.Graphics;
import java.awt.Dimension;
import javax.accessibility.Accessible;

/**
 * MultiMenuItemUI implementation
 * 
 * <p>This file was automatically generated by AutoMulti.
 *
 * @version 1.23 11/29/01 13:49:05
 * @author  Otto Multey
 */
public class MultiMenuItemUI extends MenuItemUI {

    /**
     * The Vector containing the real UI's.  This is populated 
     * in the call to createUI, and can be obtained by calling
     * getUIs.  The first element is guaranteed to the real UI 
     * obtained from the default look and feel.
     */
    protected Vector uis = new Vector();

////////////////////
// Common UI methods
////////////////////

    /**
     * Return the list of UI's associated with this multiplexing UI.  This 
     * allows processing of the UI's by an application aware of multiplexing 
     * UI's on components.
     */
    public ComponentUI[] getUIs() {
        return MultiLookAndFeel.uisToArray(uis);
    }

////////////////////
// MenuItemUI methods
////////////////////

////////////////////
// ButtonUI methods
////////////////////

////////////////////
// ComponentUI methods
////////////////////

    /**
     * Call installUI on each UI handled by this MultiUI.
     */
    public void installUI(JComponent a) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).installUI(a);
        }
    }

    /**
     * Call uninstallUI on each UI handled by this MultiUI.
     */
    public void uninstallUI(JComponent a) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).uninstallUI(a);
        }
    }

    /**
     * Call paint on each UI handled by this MultiUI.
     */
    public void paint(Graphics a, JComponent b) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).paint(a,b);
        }
    }

    /**
     * Call update on each UI handled by this MultiUI.
     */
    public void update(Graphics a, JComponent b) {
        for (int i = 0; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).update(a,b);
        }
    }

    /**
     * Call getPreferredSize on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public Dimension getPreferredSize(JComponent a) {
        Dimension returnValue = 
            ((ComponentUI) (uis.elementAt(0))).getPreferredSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getPreferredSize(a);
        }
        return returnValue;
    }

    /**
     * Call getMinimumSize on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public Dimension getMinimumSize(JComponent a) {
        Dimension returnValue = 
            ((ComponentUI) (uis.elementAt(0))).getMinimumSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getMinimumSize(a);
        }
        return returnValue;
    }

    /**
     * Call getMaximumSize on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public Dimension getMaximumSize(JComponent a) {
        Dimension returnValue = 
            ((ComponentUI) (uis.elementAt(0))).getMaximumSize(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getMaximumSize(a);
        }
        return returnValue;
    }

    /**
     * Call contains on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public boolean contains(JComponent a, int b, int c) {
        boolean returnValue = 
            ((ComponentUI) (uis.elementAt(0))).contains(a,b,c);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).contains(a,b,c);
        }
        return returnValue;
    }

    /**
     * Return a multiplexing UI instance if any of the auxiliary
     * LookAndFeels support this UI.  Otherwise, just return a 
     * UI obtained using the normal methods.
     */
    public static ComponentUI createUI(JComponent a) {
        ComponentUI mui = new MultiMenuItemUI();
        return MultiLookAndFeel.createUIs(mui,
                                          ((MultiMenuItemUI) mui).uis,
                                          a);
    }

    /**
     * Call getAccessibleChildrenCount on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public int getAccessibleChildrenCount(JComponent a) {
        int returnValue = 
            ((ComponentUI) (uis.elementAt(0))).getAccessibleChildrenCount(a);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getAccessibleChildrenCount(a);
        }
        return returnValue;
    }

    /**
     * Call getAccessibleChild on each UI handled by this MultiUI.
     * Return only the value obtained from the first UI, which is
     * the UI obtained from the default LookAndFeel.
     */
    public Accessible getAccessibleChild(JComponent a, int b) {
        Accessible returnValue = 
            ((ComponentUI) (uis.elementAt(0))).getAccessibleChild(a,b);
        for (int i = 1; i < uis.size(); i++) {
            ((ComponentUI) (uis.elementAt(i))).getAccessibleChild(a,b);
        }
        return returnValue;
    }
}
