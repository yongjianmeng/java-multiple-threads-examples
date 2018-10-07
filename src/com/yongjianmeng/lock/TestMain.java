package com.yongjianmeng.lock;

import java.util.ArrayList;
import java.util.List;

public class TestMain {

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;

//        SpinLock lock = new SpinLock();
//        TicketLock lock = new TicketLock();
//        CLHLock lock = new CLHLock();
        MCSLock lock = new MCSLock();
        List<Thread> threads = new ArrayList<>();

        for(int i = 0; i < threadCount; i++) {
            threads.add(new Thread(() -> {
                lock.lock();
                String threadName = Thread.currentThread().getName();
                System.out.println(threadName + " got lock");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(threadName + " release lock");
                lock.unlock();
            }));
        }

        for (int i = 0; i < threadCount; i++) {
            threads.get(i).setName("Thread-" + (i + 1));
            threads.get(i).start();
        }

        for (int i = 0; i < threadCount; i++) {
            threads.get(i).join();
        }

        System.out.println("It's over");
    }

}
