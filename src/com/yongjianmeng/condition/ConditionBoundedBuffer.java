package com.yongjianmeng.condition;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ConditionBoundedBuffer<T> {
    private final int BUFFER_SIZE = 10;

    protected final Lock lock = new ReentrantLock();
    // condition: notFull (count < items.length)
    private final Condition notFull = lock.newCondition();
    // condition: notEmpty (count > 0)
    private final Condition notEmpty = lock.newCondition();

    private final T[] items = (T[]) new Object[BUFFER_SIZE];
    private int tail, head, count;

    public void put(T x) throws InterruptedException {
        lock.lock();
        try {
            while (count == items.length) {
                System.out.println("Full, await not full...");
                notFull.await();
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            System.out.println("Send not empty signal");
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public T take() throws InterruptedException {
        lock.lock();
        try {
            while(count == 0) {
                System.out.println("Empty, await not empty...");
                notEmpty.await();
            }
            T x = items[head];
            items[head] = null;
            if (++head == items.length) {
                head = 0;
            }
            --count;
            System.out.println("Send not full signal");
            notFull.signal();
            return x;
        } finally {
            lock.unlock();
        }
    }
}
