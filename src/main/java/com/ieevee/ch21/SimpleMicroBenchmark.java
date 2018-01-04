package com.ieevee.ch21;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bottle on 1/4/18.
 */

interface Increament {
    void increment();
}

class SyncLock implements Increament{
    private int count=0;
    public synchronized void increment() {
        count++;
    }
}

class ReentrantLockTest implements Increament {
    private int count=0;
    private ReentrantLock lock = new ReentrantLock();
    public void increment() {
        lock.lock();
        count++;
        lock.unlock();
    }
}

public class SimpleMicroBenchmark {
    static long test(Increament increament) {
        long start = System.nanoTime();
        for (int i=0; i< 1000000; i++) {
            increament.increment();
        }
        return System.nanoTime() - start;
    }

    public static void main(String args[]) {
        long time1 = test(new SyncLock());
        long time2 = test(new ReentrantLockTest());

        System.out.println("time1 "+ time1 + ", time2 " + time2);
    }
}
