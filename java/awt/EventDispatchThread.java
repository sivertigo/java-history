/*
 * @(#)EventDispatchThread.java	1.22 01/12/10
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.awt;

import java.awt.event.MouseEvent;
import java.awt.event.KeyEvent;
import java.awt.event.PaintEvent;
import java.awt.peer.ActiveEvent;

/**
 * EventDispatchThread is a package-private AWT class which takes
 * events off the EventQueue and dispatches them to the appropriate
 * AWT components.
 *
 * @version 1.22 12/10/01
 * @author Tom Ball
 * @author Amy Fowler
 */
class EventDispatchThread extends Thread {
    private EventQueue theQueue;
    private boolean doDispatch = true;

    EventDispatchThread(String name, EventQueue queue) {
	super(name);
        theQueue = queue;
    }

    void stopDispatchingNoJoin() {
	doDispatch = false;
	theQueue.postEvent(new EmptyEvent());
    }
    public void stopDispatching() {
        doDispatch = false;
	// fix 4128923
	// post an empty event to ensure getNextEvent
	// is unblocked - rkhan 4/14/98
	// TODO: Look into using Thread.interrupt() instead
	theQueue.postEvent(new EmptyEvent());
	// wait for the dispatcher to complete
	if (Thread.currentThread() != this) {
	    try {
		join();
	    } catch(InterruptedException e) {
	    }
	}
    }

    class EmptyEvent extends AWTEvent implements ActiveEvent {
	public EmptyEvent() {
	    super(EventDispatchThread.this,0);
	}

	public void dispatch() {}
    }

    public void run() {
       while (doDispatch) {
            try {
                AWTEvent event = theQueue.getNextEvent();
                if (false) {
                    // Not until 1.2...
                    // theQueue.dispatchEvent(event);
                } else {
                    // old code...
                    Object src = event.getSource();
                    if (event instanceof ActiveEvent) {
			// This could become the sole method of dispatching in time, and 
			// moved to the event queue's dispatchEvent() method.
			((ActiveEvent)event).dispatch();
		    } else if (src instanceof Component) {
                        ((Component)src).dispatchEvent(event);
                    } else if (src instanceof MenuComponent) {
                        ((MenuComponent)src).dispatchEvent(event);
		    }
                }
            } catch (ThreadDeath death) {
                return;

            } catch (Throwable e) {
                System.err.println(
                    "Exception occurred during event dispatching:");
                e.printStackTrace();
            }
        }
    }
}
