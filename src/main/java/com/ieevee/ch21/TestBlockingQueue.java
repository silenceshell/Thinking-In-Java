package com.ieevee.ch21;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.*;

/**
 * test blockingQueue
 */
public class TestBlockingQueue implements Runnable{
    private BlockingQueue<LiftOff> queue;
    TestBlockingQueue(BlockingQueue queue) {
        this.queue = queue;
    }

    void add(LiftOff lo) {
        try {
            queue.put(lo);
        } catch (InterruptedException e) {
            System.out.println("Interrupted in put lo to queue");
        }
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
        Thread thread = new Thread(testBlockingQueue);
        thread.start();

        for (int i = 0; i< 5; i++) {
            testBlockingQueue.add(new LiftOff(i));
        }

        try {
            TimeUnit.MILLISECONDS.sleep(100);
            getKey(message);
            thread.interrupt();
            thread.join();
        } catch (InterruptedException e) {
            System.out.println("Interrupted in sleep.");
        }
    }

    public static void main(String[] args) {
        test("LinkedBlockingQueue", new LinkedBlockingQueue<LiftOff>());
        test("ArrayBlockingQueue", new ArrayBlockingQueue<LiftOff>(3));
        test("SynchronousQueue", new SynchronousQueue<LiftOff>());
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


