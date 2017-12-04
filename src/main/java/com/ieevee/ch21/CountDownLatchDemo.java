package com.ieevee.ch21;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * CountDownLatchDemo
 */
public class CountDownLatchDemo {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(10);
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i=0; i<3; i++) {
            service.execute(new WaitingTask(latch));
        }
        try {
            for (int i=0; i<10; i++) {
                service.execute(new PortionTask(latch));
            }

            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdownNow();
    }
}

class PortionTask implements Runnable {
    CountDownLatch latch;
    Random random = new Random(47);
    PortionTask(CountDownLatch latch) {
        this.latch = latch;
    }
    public void run() {
        try {
            doWork();
            latch.countDown();
            System.out.println("latch count down.");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void doWork() throws InterruptedException{
        TimeUnit.MILLISECONDS.sleep(random.nextInt(2000));
    }
}

class WaitingTask implements Runnable {
    CountDownLatch latch;
    WaitingTask(CountDownLatch latch) {
        this.latch = latch;
    }
    public void run() {
        try {
            System.out.println("waiting latch to zero");
            latch.await();
            System.out.println("latch is set to zero");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}