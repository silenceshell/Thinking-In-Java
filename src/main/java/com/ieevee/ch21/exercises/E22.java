package com.ieevee.ch21.exercises;

import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * exercise 22
 */
public class E22 {
    public static void main(String[] args) {
        Change change = new Change(false);

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(new SleepAndSetTrue(change));
        service.execute(new BusyWaiting(change));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.execute(new JustWait(change));

        service.shutdown();
        try {
            boolean result = service.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println(result);
            service.shutdownNow();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Change {
    boolean x;
    Change(boolean x) {
        this.x = x;
    }
    synchronized void setX(boolean x) {
        this.x = x;
    }
    synchronized boolean getX() {
        return this.x;
    }

    synchronized void _wait() throws InterruptedException{
        wait();
    }
    synchronized void _notify() {
        notify();
    }

}

class SleepAndSetTrue implements Runnable {
    private Change change;
    SleepAndSetTrue(Change change) {
        this.change = change;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.SECONDS.sleep(3);
                change._notify();
                change.setX(true);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class BusyWaiting implements Runnable{
    private Change change;
    BusyWaiting(Change change) {
        this.change = change;
    }
    public void run() {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        while (!Thread.interrupted()) {
        long start = System.nanoTime();
        while (!change.x) {
            if (change.getX()) {
                System.out.printf("set true! BusyWaiting finished, used %d nano seconds.\r\n", System.nanoTime() - start);
//                change.setX(false);
            }
        }
    }
}

class JustWait implements Runnable{
    private Change change;
    JustWait(Change change) {
        this.change = change;
    }
    public void run() {
        long start = System.nanoTime();
//        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
//            while(!Thread.interrupted()) {
                change._wait();
                if (change.getX()) {
                    System.out.printf("set true! JustWait finished, used %d nano seconds ", System.nanoTime() - start);
//                    change.setX(false);
                }
//            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}