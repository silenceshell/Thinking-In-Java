package com.ieevee.ch21.exercises;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * exercise 28
 */
public class E28 {
    public static void main(String[] args) {
        TestBlockingQueue.test("LinkedBlockingQueue", new LinkedBlockingQueue<LiftOff>());
        TestBlockingQueue.test("ArrayBlockingQueue", new ArrayBlockingQueue<LiftOff>(3));
        TestBlockingQueue.test("SynchronousQueue", new SynchronousQueue<LiftOff>());
    }
}

class TestBlockingQueue implements Runnable{
    private BlockingQueue<LiftOff> queue;
    TestBlockingQueue(BlockingQueue queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                LiftOff liftOff = queue.take();
                liftOff.run();
            }
        } catch (InterruptedException e) {
            System.out.println("This thread is killed.");
        }
    }

    static void getkey() {
        try {
            new BufferedReader(new InputStreamReader(System.in)).readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    static void getKey(String message) {
        System.out.println("please press ENTER if you see all the LiftOffs");
        getkey();
    }

    static void test(String message, BlockingQueue queue) {
        TestBlockingQueue testBlockingQueue = new TestBlockingQueue(queue);
        Thread readThread = new Thread(testBlockingQueue);
        readThread.start();

        Thread insertThread = new Thread(new InsertToBlockingQueue(queue));
        insertThread.start();

        try {
            TimeUnit.MILLISECONDS.sleep(100);
            getKey(message);
            readThread.interrupt();
            readThread.join();
            insertThread.interrupt();
            insertThread.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted in sleep.");
        }
    }
}

class InsertToBlockingQueue implements Runnable{
    private BlockingQueue<LiftOff> queue;
    InsertToBlockingQueue(BlockingQueue<LiftOff> queue) {
        this.queue = queue;
    }

    void add(LiftOff lo) {
        try {
            queue.put(lo);
        } catch (InterruptedException e) {
            System.out.println("Interrupted in putting lo to queue");
        }
    }

    public void run() {
        for (int i = 0; i< 5; i++) {
            add(new LiftOff(i));
        }
    }
}

class LiftOff {
    protected int value;
    LiftOff(int value) {
        this.value = value;
    }

    protected void run() {
        System.out.println(value);
    }
}