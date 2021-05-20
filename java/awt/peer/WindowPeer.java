/*
 * @(#)WindowPeer.java	1.5 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt.peer;

import java.awt.*;

public interface WindowPeer extends ContainerPeer {
    void toFront();
    void toBack();
}


