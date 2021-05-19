/*
 * @(#)BasicMenuBarUI.java	1.72 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.plaf.basic;

import javax.swing.*;
import javax.swing.event.*;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.border.*;
import javax.swing.plaf.*;


/**
 * A default L&F implementation of MenuBarUI.  This implementation
 * is a "combined" view/controller.
 *
 * @version 1.72 11/29/01
 * @author Georges Saab
 * @author David Karlton
 * @author Arnaud Weber
 */
public class BasicMenuBarUI extends MenuBarUI  {
    protected JMenuBar              menuBar = null;
    protected ContainerListener     containerListener;
    protected ChangeListener        changeListener;
    private PropertyChangeListener  propertyChangeListener;

    public static ComponentUI createUI(JComponent x) {
	return new BasicMenuBarUI();
    }

    public void installUI(JComponent c) {
	menuBar = (JMenuBar) c;

	installDefaults();
        installListeners();
        installKeyboardActions();

    }

    protected void installDefaults() {
	if (menuBar.getLayout() == null ||
	    menuBar.getLayout() instanceof UIResource) {
            if( BasicGraphicsUtils.isLeftToRight(menuBar) ) {
                menuBar.setLayout(new DefaultMenuLayout(menuBar,BoxLayout.X_AXIS));
            } else {
                menuBar.setLayout(new RightToLeftMenuLayout());
            }
        }
	menuBar.setOpaque(true);
	LookAndFeel.installBorder(menuBar,"MenuBar.border");
	LookAndFeel.installColorsAndFont(menuBar,
					      "MenuBar.background",
					      "MenuBar.foreground",
					      "MenuBar.font");
    }

    protected void installListeners() {
        containerListener = createContainerListener();
        changeListener = createChangeListener();
        propertyChangeListener = createPropertyChangeListener();
	
        for (int i = 0; i < menuBar.getMenuCount(); i++) {
            JMenu menu = menuBar.getMenu(i);
	    if (menu!=null)
		menu.getModel().addChangeListener(changeListener);        
	}
	menuBar.addContainerListener(containerListener);
        menuBar.addPropertyChangeListener(propertyChangeListener);
    }

    protected void installKeyboardActions() {
	menuBar.registerKeyboardAction(
		new TakeFocus(menuBar),
		KeyStroke.getKeyStroke(KeyEvent.VK_F10,
				       0,
				       false),
		JComponent.WHEN_IN_FOCUSED_WINDOW);
    } 

    public void uninstallUI(JComponent c) {
        uninstallDefaults();
        uninstallListeners();
        uninstallKeyboardActions();

	menuBar = null;
    }

    protected void uninstallDefaults() {
	LookAndFeel.uninstallBorder(menuBar);
    }

    protected void uninstallListeners() {
	menuBar.removeContainerListener(containerListener);
        menuBar.removePropertyChangeListener(propertyChangeListener);

        for (int i = 0; i < menuBar.getMenuCount(); i++) {
	    JMenu menu = menuBar.getMenu(i);
	    if (menu !=null)
		menu.getModel().removeChangeListener(changeListener);
        }

	containerListener = null;
	changeListener = null;
        propertyChangeListener = null;
    }

    protected void uninstallKeyboardActions() {
	menuBar.unregisterKeyboardAction(
		       KeyStroke.getKeyStroke(KeyEvent.VK_F10,
					      0,
					      false));
    }

    protected ContainerListener createContainerListener() {
	return new ContainerHandler();
    }

    protected ChangeListener createChangeListener() {
        return new ChangeHandler();
    }

    private PropertyChangeListener createPropertyChangeListener() {
        return new PropertyChangeHandler();
    }

    private class ChangeHandler implements ChangeListener {
	public void stateChanged(ChangeEvent e) {
	    int i,c;
	    for(i=0,c = menuBar.getMenuCount() ; i < c ; i++) {
		JMenu menu = menuBar.getMenu(i);
		if(menu !=null && menu.isSelected()) {
		    menuBar.getSelectionModel().setSelectedIndex(i);
		    break;
		}
	    }
	}
    }

    /*
     * This PropertyChangeListener is used to adjust the default layout
     * manger when the menuBar is given a right-to-left ComponentOrientation.
     * This is a hack to work around the fact that the DefaultMenuLayout
     * (BoxLayout) isn't aware of ComponentOrientation.  When BoxLayout is
     * made aware of ComponentOrientation, this listener will no longer be
     * necessary.
     */
    private class PropertyChangeHandler implements PropertyChangeListener {
        public void propertyChange(PropertyChangeEvent e) {
            String name = e.getPropertyName();
            if( name.equals("componentOrientation")
                && (menuBar.getLayout() instanceof UIResource) )
            {
                if( BasicGraphicsUtils.isLeftToRight(menuBar) ) {
                    menuBar.setLayout(new DefaultMenuLayout(menuBar,BoxLayout.X_AXIS));
                } else {
                    menuBar.setLayout(new RightToLeftMenuLayout());
                }
            }
        }
    }
    
    public Dimension getPreferredSize(JComponent c) {
        return null;
    }

    public Dimension getMinimumSize(JComponent c) {
        return null;
    }

    public Dimension getMaximumSize(JComponent c) {
        return null;
    }

    private class ContainerHandler implements ContainerListener {
	public void componentAdded(ContainerEvent e) {
	    Component c = e.getChild();
	    if (c instanceof JMenu)
		((JMenu)c).getModel().addChangeListener(changeListener);
	}
	public void componentRemoved(ContainerEvent e) {
	    Component c = e.getChild();
	    if (c instanceof JMenu)
		((JMenu)c).getModel().removeChangeListener(changeListener);
	}
    }


    private static class TakeFocus implements ActionListener {
	JMenuBar menuBar;

        TakeFocus(JMenuBar menuBar) {
	    this.menuBar = menuBar;
	}
	
	public void actionPerformed(ActionEvent e) {
            MenuSelectionManager defaultManager = MenuSelectionManager.defaultManager();
	    MenuElement me[];
	    MenuElement subElements[];
	    JMenu menu = menuBar.getMenu(0);
	    if (menu!=null) {
		    me = new MenuElement[3];
		    me[0] = (MenuElement) menuBar;
		    me[1] = (MenuElement) menu;
		    me[2] = (MenuElement) menu.getPopupMenu();
		    defaultManager.setSelectedPath(me);
	    }
	}
	public boolean isEnabled() {
	    return menuBar.isEnabled();
	}
    }

    private static class RightToLeftMenuLayout
        extends FlowLayout implements UIResource
    {
        private RightToLeftMenuLayout() {
            super(3/*FlowLayout.LEADING*/,0,0);
        }
    }
}


