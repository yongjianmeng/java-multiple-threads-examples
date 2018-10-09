package com.yongjianmeng.condition;

import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        int threadCount = 10;

        ConditionBoundedBuffer buffer = new ConditionBoundedBuffer<Integer>();

        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < threadCount; i++) {
            int finalI = i;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    for(int j = 0; j < 1000; j++) {
                        if (finalI % 2 == 0) {
                            try {
                                buffer.put(1);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        } else {
                            try {
                                buffer.take();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
            threads.add(thread);
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
