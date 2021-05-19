/*
 * @(#)ReferenceQueue.java	1.21 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.ref;


/**
 * Reference queues, to which registered reference objects are appended by the
 * garbage collector after the appropriate reachability changes are detected.
 *
 * @version  1.21, 01/11/29
 * @author   Mark Reinhold
 * @since    JDK1.2
 */

public class ReferenceQueue {

    /**
     * Constructs a new reference-object queue.
     */
    public ReferenceQueue() { }

    private static class Null extends ReferenceQueue {
	boolean enqueue(Reference r) {
	    return false;
	}
	void enqueueTemporary(Reference r) { }
    }

    static ReferenceQueue NULL = new Null();
    static ReferenceQueue ENQUEUED = new Null();

    static private class Lock { };
    private Lock lock = new Lock();
    private Reference head = null;

    boolean firstTouch = true;
    ReferenceQueue nextTouched;
    private Reference tempHead, tempTail;
    
    void enqueueTemporary(Reference r) {
         synchronized(r) {
	     /* "r" could only be enqueued on a pending queue if
	        it was discovered while active by the collector.
		As such, its queue is still this ReferenceQueue
		and its next field is not null.  This last condition 
		prevents a thread from actively enqueuing "r". */
             r.queue = ENQUEUED;
             if (tempHead == null) {  /* Adding the first one? */
                 tempTail = r;
                 r.next = r;    /* Never set "r.next" to null! */
             } else {
                 r.next = tempHead;
             }
             tempHead = r;
         }
    }

    void spliceQueues() {
        if (tempHead != null) {
            synchronized(lock) {
	        if (head == null) {
                    lock.notifyAll();
                    head = tempHead;       /* tempHead is already tail-tied */
                } else {
	            tempTail.next = head;  /* head is already tail-tied */
	            head = tempHead;
                }
            }
	    tempHead = tempTail = null;
        }
    }

    boolean enqueue(Reference r) {	/* Called only by Reference class */
	synchronized (lock) {           /* Get the locks in the right order */
	    synchronized (r) {
	        /* We assume that a collection may not suspend a thread
		   in this method between the test finding r.next to be null
		   and the assignment to r.next. */
	        if (r.next != null) return false;
		r.queue = ENQUEUED;
		if (head == null) {
		    r.next = r;         /* Tie the tail. Ouch! */
		    lock.notifyAll();
		} else {
		    r.next = head;
		}
		head = r;
	    }
	}
	return true;
    }

    private Reference reallyPoll() {	/* Must hold lock */
	if (head != null) {
	    Reference r = head;
            synchronized(r) {
	        head = (r.next == r) ? null : r.next;
	        r.queue = NULL;
	        r.next = r;
            }
	    return r;
	}
	return null;
    }

    /**
     * Polls this queue to see if a reference object is available,
     * returning one immediately if so.  If the queue is empty, this
     * method immediately returns <code>null</code>.
     *
     * @return  A reference object, if one was immediately available,
     *          otherwise <code>null</code>
     */
    public Reference poll() {
	synchronized (lock) {
	    return reallyPoll();
	}
    }

    /**
     * Removes the next reference object in this queue, blocking until either
     * one becomes available or the given timeout period expires.
     *
     * @param  timeout  If positive, block for up <code>timeout</code>
     *                  milliseconds while waiting for a reference to be
     *                  added to this queue.  If zero, block indefinitely.
     *
     * @return  A reference object, if one was available within the specified
     *          timeout period, otherwise <code>null</code>
     *
     * @throws  IllegalArgumentException
     *          If the value of the timeout argument is negative
     *
     * @throws  InterruptedException
     *          If the timeout wait is interrupted
     */
    public Reference remove(long timeout)
	throws IllegalArgumentException, InterruptedException
    {
	if (timeout < 0) {
	    throw new IllegalArgumentException("Negative timeout value");
	}
	synchronized (lock) {
	    Reference r = reallyPoll();
	    if (r != null) return r;
	    for (;;) {
		lock.wait(timeout);
		r = reallyPoll();
		if (r != null) return r;
		if (timeout != 0) return null;
	    }
	}
    }

    /**
     * Removes the next reference object in this queue, blocking until one
     * becomes available.
     *
     * @throws  InterruptedException  If the wait is interrupted
     */
    public Reference remove() throws InterruptedException {
	return remove(0);
    }
}

