/*
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package org.omg.CosNaming;


/**
* org/omg/CosNaming/BindingIteratorOperations.java
* Generated by the IDL-to-Java compiler (portable), version "3.0"
* from nameservice.idl
* Wednesday, January 26, 2000 3:34:52 PM PST
*/

public interface BindingIteratorOperations
{
    /**
     * This operation returns the next binding. If there are no more
     * bindings, false is returned.
     ** @param b the returned binding
     */
  boolean next_one (org.omg.CosNaming.BindingHolder b);
    /**
     * This operation returns at most the requested number of bindings.
     ** @param how_many the maximum number of bindings tro return <p>
     ** @param bl the returned bindings
     */
  boolean next_n (int how_many, org.omg.CosNaming.BindingListHolder bl);

    /**
     * This operation destroys the iterator.
     */
  void destroy ();
} // interface BindingIteratorOperations
