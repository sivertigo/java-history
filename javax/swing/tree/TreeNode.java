/*
 * @(#)TreeNode.java	1.15 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package javax.swing.tree;

import java.util.Enumeration;

/**
 * Defines the requirements for an object that can be used as a
 * tree node in a JTree.
 *
 * @version 1.15 11/29/01
 * @author Rob Davis
 * @author Scott Violet
 */

public interface TreeNode
{
    /**
     * Returns the child <code>TreeNode</code> at index 
     * <code>childIndex</code>.
     */
    TreeNode getChildAt(int childIndex);

    /**
     * Returns the number of children <code>TreeNode</code>s the receiver
     * contains.
     */
    int getChildCount();

    /**
     * Returns the parent <code>TreeNode</code> of the receiver.
     */
    TreeNode getParent();

    /**
     * Returns the index of <code>node</code> in the receivers children.
     * If the receiver does not contain <code>node</code>, -1 will be
     * returned.
     */
    int getIndex(TreeNode node);

    /**
     * Returns true if the receiver allows children.
     */
    boolean getAllowsChildren();

    /**
     * Returns true if the receiver is a leaf.
     */
    boolean isLeaf();

    /**
     * Returns the children of the reciever as an Enumeration.
     */
    Enumeration children();
}
