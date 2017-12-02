package com.ieevee.ch21;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * notify vs notifyAll
 */
public class NotifyVsNotifyAll{
    public static void main(String[] args) throws Exception{
        ExecutorService service = Executors.newCachedThreadPool();
        for (int i= 0; i< 5; i++) {
            service.execute(new Task1());
        }
        service.execute(new Task2());
        service.shutdown();

        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            private boolean isAll = false;
            @Override
            public void run() {
                if (isAll) {
                    Task1.blocker.probAll();
                    isAll = false;
                } else {
                    Task1.blocker.prob();
                    isAll = true;
                }
            }
        }, 1000, 1000);
        TimeUnit.SECONDS.sleep(5);
        timer.cancel();
        Task2.blocker.probAll();

        service.shutdownNow();
    }
}

class Blocker {
    synchronized void prob() {
        notify();
    }
    synchronized void probAll() {
        notifyAll();
    }
    synchronized void waittingCall() throws InterruptedException{
        wait();
    }
}

class Task1 implements Runnable {
    static Blocker blocker = new Blocker();
    Task1() {
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                blocker.waittingCall();
                System.out.println("get called in Task1" + Thread.currentThread());
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("get interrupt." + Thread.currentThread());
        }
    }
}

class Task2 implements Runnable {
    static Blocker blocker = new Blocker();
    public void run() {
        try {
            while (!Thread.interrupted()) {
                blocker.waittingCall();
                System.out.println("get called in Task2" + Thread.currentThread());
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("get interrupt." + Thread.currentThread());
        }
    }
}