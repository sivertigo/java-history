/*
 * @(#)Canvas.java	1.19 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */
package java.awt;

import java.awt.peer.CanvasPeer;

/**
 * A <code>Canvas</code> component represents a blank rectangular 
 * area of the screen onto which the application can draw or from 
 * which the application can trap input events from the user. 
 * <p>
 * An application must subclass the <code>Canvas</code> class in 
 * order to get useful functionality such as creating a custom 
 * component. The <code>paint</code> method must be overridden 
 * in order to perform custom graphics on the canvas.
 *
 * @version 	1.19 12/10/01
 * @author 	Sami Shaio
 * @since       JDK1.0
 */
public class Canvas extends Component {

    private static final String base = "canvas";
    private static int nameCounter = 0;

    /*
     * JDK 1.1 serialVersionUID 
     */
     private static final long serialVersionUID = -2284879212465893870L;

    /** 
     * Constructs a new Canvas.
     */
    public Canvas() {
    }

    /**
     * Construct a name for this component.  Called by getName() when the
     * name is null.
     */
    String constructComponentName() {
        return base + nameCounter++;
    }

    /**
     * Creates the peer of the canvas.  This peer allows you to change the 
     * user interface of the canvas without changing its functionality.
     * @see     java.awt.Toolkit#createCanvas(java.awt.Canvas)
     * @see     java.awt.Component#getToolkit()
     * @since   JDK1.0
     */
    public void addNotify() {
        synchronized (getTreeLock()) {
		if (peer == null)
	    	peer = getToolkit().createCanvas(this);

	    super.addNotify();
        }
    }

    /**
     * This method is called to repaint this canvas. Most applications 
     * that subclass <code>Canvas</code> should override this method in 
     * order to perform some useful operation. 
     * <p>
     * The <code>paint</code> method provided by <code>Canvas</code> 
     * redraws this canvas's rectangle in the background color. 
     * <p>
     * The graphics context's origin (0,&nbsp;0) is the top-left corner 
     * of this canvas. Its clipping region is the area of the context. 
     * @param      g   the graphics context.
     * @see        java.awt.Graphics
     * @since      JDK1.0
     */
    public void paint(Graphics g) {
	g.setColor(getBackground());
	g.fillRect(0, 0, width, height);
    }

    boolean postsOldMouseEvents() {
        return true;
    }
}
