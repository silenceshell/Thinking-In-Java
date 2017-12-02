package com.ieevee.ch21;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * wax on matic 2 with condition
 */
public class WaxOnMatic2 {
    Car car = new Car();
    ExecutorService service = Executors.newCachedThreadPool();
    WaxOnBoy waxOnBoy = new WaxOnBoy(car);
    BuffOnBoy buffOnBoy = new BuffOnBoy(car);

    WaxOnMatic2() {
        service.execute(waxOnBoy);
        service.execute(buffOnBoy);
        try {
            TimeUnit.SECONDS.sleep(7);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        service.shutdownNow();
    }

    public static void main(String[] args) {
        new WaxOnMatic2();
    }

    class Car {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        boolean waxed;
        void waxOn() throws InterruptedException{
            lock.lock();

            try {
                TimeUnit.SECONDS.sleep(1);
                waxed = true;
                System.out.println("wax on");
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        void waxOff() throws InterruptedException{
            lock.lock();
            try {
                TimeUnit.SECONDS.sleep(1);
                waxed = false;
                System.out.println("wax off");
                condition.signalAll();
            } finally {
                lock.unlock();
            }
        }

        void waitForWax() throws InterruptedException{
            lock.lock();
            try {
                while (!waxed) {
                    System.out.println("wait for wax");
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }
        void waitForBuff() throws InterruptedException{
            lock.lock();
            try {
                while (waxed) {
                    System.out.println("wait for buff");
                    condition.await();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    class WaxOnBoy implements Runnable{
        Car car;
        WaxOnBoy(Car car) {
            this.car = car;
        }
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    this.car.waitForBuff();
                    car.waxOn();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    class BuffOnBoy implements Runnable{
        Car car;
        BuffOnBoy(Car car) {
            this.car = car;
        }
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    this.car.waitForWax();
                    car.waxOff();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



}