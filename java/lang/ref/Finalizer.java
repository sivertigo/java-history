/*
 * @(#)Finalizer.java	1.23 01/11/29
 *
 * Copyright 2002 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package java.lang.ref;

import java.security.PrivilegedAction;
import java.security.AccessController;


final class Finalizer extends FinalReference { /* Package-private; must be in
						  same package as the Reference
						  class */
    /* Current number of Finalizer worker threads. */
    private static volatile int num_workers = 1;

    /* Maximum permissible number of Finalizer worker threads;
       We may want to have this be some multiple of number of CPU's
       if/when we are able to query the platform for such information. */
    private final static int MAX_WORKERS = 8;

    /* All the worker threads (including slots for those not yet created). */
    static FinalizerWorker workers[] = new FinalizerWorker[MAX_WORKERS];

    private static class FinalizerWorker {
        ReferenceQueue toBeFinalized = new ReferenceQueue();
        private Finalizer unfinalized;
        protected int numPending;
        // The THRESHOLD value should be dynamically configurable;
        // not clear what the strategy should be. The current
        // strategy is somewhat too simple-minded.
        protected int THRESHOLD = 100000;

        private synchronized void add(Finalizer f) {
            if (unfinalized != null) {
                f.nextF = unfinalized;
                unfinalized.prevF = f;
            }
            unfinalized = f;
            numPending++;
            if (numPending % 10000 == 0 && num_workers < MAX_WORKERS) {
                if (numPending > THRESHOLD) {
                    startNewFinalizer(this);
                }
            }
        }
    
        private synchronized void remove(Finalizer f) {
            Finalizer nxt = f.nextF, prv = f.prevF;
            if (unfinalized == f) {
                if (nxt != null) {
                    unfinalized = nxt;
                } else {
                    unfinalized = null; /* isn't something fishy here. */
                }
            }
            if (nxt != null) {
                nxt.prevF = prv;
            }
            if (prv != null) {
                prv.nextF = nxt;
            }
            numPending--;
        }
    
        private synchronized Finalizer removeFirst() {
            Finalizer f = unfinalized;
            if (f != null) remove(f);
            return f;
        }
    
        private void finalizeOne(Finalizer f) {
            /* The "synchronized" block is necessary in the case that we are 
               running a secondary finalizer thread in this FinalizerWorker. */
            /* assert(f != null); */
            synchronized (f) {
                if (f.nextF != f) {   /* Has it already been finalized? */
                    remove(f);
                    f.nextF = f;      /* Now it has been finalized! */
                }
            }
            try {
                Object finalizee = f.get();
                if (finalizee != null) {
                    invokeFinalizeMethod(finalizee);
                }
            } catch (Throwable x) { }
            f.clear();
        }
    
        private void runFinalization() {
            for (;;) {
                Finalizer f = (Finalizer)toBeFinalized.poll();
                if (f == null) return;
                finalizeOne(f);
            }
        }
    
        private void runFinalizersOnExit() {
            for (;;) {
                Finalizer f = removeFirst();
                if (f == null) return;
                finalizeOne(f);
            }
        }
    
        private class FinalizerThread extends Thread {
            FinalizerThread(ThreadGroup g) {
                super(g, "Finalizer");
            }
            public void run() {
                for (;;) {
                    try {
                        Finalizer f = (Finalizer)(toBeFinalized.remove());
                        if (f != null)
                            finalizeOne(f);
                    } catch (InterruptedException x) {
                        continue;
                    }
                }
            }
        }
    
        FinalizerWorker(ThreadGroup tg) {
            Thread finalizer = new FinalizerThread(tg);
            finalizer.setPriority(Thread.MAX_PRIORITY - 2);
            finalizer.setDaemon(true);
            finalizer.start();
        }
    }

    /* A native method that invokes an arbitrary object's finalize method is
       required since the finalize method is protected
     */
    static native void invokeFinalizeMethod(Object o) throws Throwable;

    private Finalizer
        nextF = null,	// nextF == this indicates that finalizer has been run
	prevF = null;

    private Finalizer(Object finalizee, FinalizerWorker w) {
	super(finalizee, w.toBeFinalized);
	w.add(this);
    }

    static volatile int lastWorkerIdx = 0;

    /* Invoked by VM */
    static void register(Object finalizee) {
        /* Unsync. access to "lastWorkerIdx" is preferable for efficiency.
           The resulting non-determinism is innocuous since we use it for
           work balancing only. */
        int idx = lastWorkerIdx + 1;
        if (idx == num_workers) idx = 0;
        lastWorkerIdx = idx;
	new Finalizer(finalizee, workers[idx]);
    }

    /* Create a privileged secondary finalizer thread in the system thread
       group for the given Runnable, and wait for it to complete.

       This method is used by both runFinalization and runFinalizersOnExit.
       The former method invokes all pending finalizers, while the latter
       invokes all uninvoked finalizers if on-exit finalization has been
       enabled.

       These two methods could have been implemented by offloading their work
       to the regular finalizer thread and waiting for that thread to finish.
       The advantage of creating a fresh thread, however, is that it insulates
       invokers of these methods from a stalled or deadlocked finalizer thread.

       While we run multiple primary finalizer threads, (for now) we have
       use only a single secondary thread (per call).
     */
    private static void forkSecondaryFinalizer(final Runnable proc) {
        PrivilegedAction pa = new PrivilegedAction() {
            public Object run() {
                ThreadGroup tg = finalizerThreadGroup();
                Thread sft = new Thread(tg, proc, "Secondary finalizer");
                sft.start();
                try {
                    sft.join();
                } catch (InterruptedException x) {
                    /* Ignore */
                }
                return null;
            }};
        AccessController.doPrivileged(pa);
    }
    
    /* Called by Runtime.runFinalization() */
    static void runFinalization() {
        forkSecondaryFinalizer(new Runnable() {
            public void run() {
                for (int i = 0; i < num_workers; i++) {
                    workers[i].runFinalization();
                }
            }
        });
    }

    private static boolean runOnExit = false;

    /* Called by Runtime.runFinalizersOnExit */
    static void setRunFinalizersOnExit(boolean value) {
	runOnExit = value;
    }

    /* Called on exit by the VM */
    static void runFinalizersOnExit() {
	if (!runOnExit) return;
        forkSecondaryFinalizer(new Runnable() {
            public void run() {
                for (int i = 0; i < num_workers; i++) {
                    workers[i].runFinalizersOnExit();
                }
            }
        });
    }

    static ThreadGroup finalizerThreadGroup() {
        ThreadGroup tg = Thread.currentThread().getThreadGroup();
        for (ThreadGroup tgn = tg;
             tgn != null;
             tg = tgn, tgn = tg.getParent());
        return tg;
    }

    static synchronized boolean startNewFinalizer(FinalizerWorker worker) {
        if (num_workers == MAX_WORKERS) {
            // System.err.println("*** Already "+ num_workers +" finalizers");
            return false;
        }
        if (worker.numPending < worker.THRESHOLD) {
            // System.err.println("*** "+ worker.numPending +" < "+
            //                    worker.THRESHOLD);
            return false;
        }
        ThreadGroup tg = finalizerThreadGroup();
        workers[num_workers] = new FinalizerWorker(tg);
        // System.err.println("*** "+ (num_workers+1) +"th finalizer started at "+
        //                   worker.THRESHOLD);
        worker.THRESHOLD *= 2;
        num_workers++;
        return true;
    }

    static {
        ThreadGroup tg = finalizerThreadGroup();
        for (int i = 0; i < num_workers; i++) {
            workers[i] = new FinalizerWorker(tg);
        }
    }

}
