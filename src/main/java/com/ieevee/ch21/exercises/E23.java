package com.ieevee.ch21.exercises;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * exercise 23
 */
class Car {
    private boolean waxed = false;
    //    private boolean buffling = false
    synchronized void Wax() {
        waxed = true;
//        notifyAll();
        notify();
    }
    synchronized void Buffed() {
        waxed = false;
//        notifyAll();
        notify();
    }
    synchronized void WaitForWax() throws InterruptedException{
        while (!waxed) {
            wait();
        }
    }
    synchronized void WaitForBuff() throws InterruptedException{
        while (waxed) {
            wait();
        }
    }
}

class WaxOn implements Runnable {
    private Car car;
    WaxOn(Car car) {
        this.car = car;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("wax on!");
                car.Wax();
                System.out.println("wait for buff!");
                car.WaitForBuff();
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("thread is interrupted, exit now.");
        }
        System.out.println("wax on end");
    }
}
class WaxOff implements Runnable {
    private Car car;
    WaxOff(Car car) {
        this.car = car;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println("wax off!");
                car.Buffed();
                System.out.println("wait for wax!");
                car.WaitForWax();
            }
        } catch (InterruptedException e) {
//            e.printStackTrace();
            System.out.println("thread is interrupted, exit now.");
        }
        System.out.println("wax off end");
    }
}
public class E23 {
    public static void main(String[] args) {
        Car car = new Car();
        ExecutorService executorService = Executors.newCachedThreadPool();
        executorService.execute(new WaxOn(car));
        executorService.execute(new WaxOff(car));

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        executorService.shutdownNow();
    }
}