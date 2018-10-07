package com.yongjianmeng.lock;

import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;
import java.util.concurrent.locks.LockSupport;

public class BlockCLHLock {
    public static class CLHNode {
        private volatile Thread isLocked;
    }
    private volatile CLHNode tail;
    private static final ThreadLocal<CLHNode> LOCAL = new ThreadLocal<>();
    private static final AtomicReferenceFieldUpdater<BlockCLHLock, CLHNode> UPDATER =
            AtomicReferenceFieldUpdater.newUpdater(BlockCLHLock.class, CLHNode.class, "tail");

    public void lock() {
        CLHNode node = new CLHNode();
        LOCAL.set(node);
        CLHNode preNode = UPDATER.getAndSet(this, node);
        if (preNode != null) {
            preNode.isLocked = Thread.currentThread();
            LockSupport.park(this);
            preNode = null;
            LOCAL.set(node);
        }
    }

    public void unlock() {
        CLHNode node = LOCAL.get();
        if (!UPDATER.compareAndSet(this, node, null)) {
            LockSupport.unpark(node.isLocked);
        }
        node = null;
    }
}
