package com.ieevee.ch21;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class EvenIntGenerator extends IntGenerater {
    private int value = 0;
    final Integer integer = new Integer(0);
    private AtomicInteger valueAtomic = new AtomicInteger();
    private Lock lock = new ReentrantLock();
//    public synchronized int next() {

    public int next() {
//        lock.lock();
//        try {
//            value++;
        return valueAtomic.addAndGet(2);
//            Thread.yield();
//            value++;

//            return value;
//        } finally {
//            lock.unlock();
//        }
    }

    private boolean tryLock() {
        boolean getLock = false;
        if (lock.tryLock()) {
            System.out.println("get locked!");
            getLock = true;
        }

        if (getLock) {
            lock.unlock();
        }
        return getLock;
    }
    private boolean tryLockTimeout() {
        boolean getLock = false;
        try {
            if (lock.tryLock(2, TimeUnit.SECONDS)) {
                System.out.println("get locked");
                getLock = true;
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (getLock) {
            lock.unlock();
        }
        return getLock;
    }

    synchronized void f() {

        int i=0;
        while (i<5) {
            System.out.println("f()");
            Thread.yield();
            i++;
        }

    }

     void g() {
        synchronized (integer) {
            int i=0;
            while (i<5) {
                System.out.println("g()");
                Thread.yield();
                    i++;
            }
        }
    }

    public static void main(String[] args) {

        int sum = 1;
        int j = 0;
        System.out.println(1);
        for (int i=2; i<=1000; i++) {
            for (j = (i-1)/2; j>1; j--) {
                if (i%j==0) {
                    break;
                }
            }
            if (j==1) {
                sum+=1;
                System.out.println(i);
            }
        }
        System.exit(0);

        System.out.println(args.length + args[0]);
//        List list = new ArrayList();
//        List<Integer> list2 = new ArrayList<Integer>();
//        list.add(0);
//        list2.add(1);
//        System.out.println(list.get(0) instanceof Integer);
//        System.out.println(list2.get(0) instanceof Integer);
        System.exit(0);

        final EvenIntGenerator evenIntGenerator = new EvenIntGenerator();

        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                System.out.println("time ended");
                System.exit(0);
            }
        }, 3000);

        new Thread() {
            @Override
            public void run() {
                evenIntGenerator.f();
            }
        }.start();

        evenIntGenerator.g();
        Thread.yield();


//        for (int i=0; i< 100; i++) {
//        }
//        final EvenIntGenerator evenIntGenerator = new EvenIntGenerator();
//        new Thread() {
//            {setDaemon(true);}
//            public void run() {
//                evenIntGenerator.lock.lock();
//            }
//        }.start();
        System.out.println(evenIntGenerator.tryLock());
        System.out.println(evenIntGenerator.tryLockTimeout());
        EvenChecker.test(new EvenIntGenerator());
    }
}

