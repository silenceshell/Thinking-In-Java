package com.ieevee.ch21.exercises;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * exercise 27
 */
public class E27 {
    public static void main(String[] args) {
        E27 e27 = new E27();
        Restaurant restaurant = e27.new Restaurant();
    }

    class Restaurant {
        Meal meal = null;
        WaitPerson waitPerson = new WaitPerson(this);
        Chef chef = new Chef(this);
        BusBoy busBoy = new BusBoy(this);
        ExecutorService service = Executors.newCachedThreadPool();

        Restaurant() {
            service.execute(chef);
            service.execute(waitPerson);
            service.execute(busBoy);
        }
    }

    class Meal {

    }

    class BusBoy implements Runnable{
        private Restaurant restaurant;
        protected boolean isCleaned;
//        protected ReentrantLock lock = new ReentrantLock();
//        protected Condition condition = lock.newCondition();

        BusBoy(Restaurant restaurant) {
            this.restaurant = restaurant;
        }

        public void run() {
            try {
                while (!Thread.interrupted()) {
                    restaurant.waitPerson.lock.lock();
                    try {
//                        synchronized (restaurant.waitPerson) {
//                            while (isCleaned) {
//                                restaurant.waitPerson.wait();
//                            }
//                        }
                        while (isCleaned) {
                            restaurant.waitPerson.condition.await();
                        }

                        System.out.println("BusBoy: get new cleaning.");
                        TimeUnit.SECONDS.sleep(1);
                        isCleaned = true;
                        System.out.println("BusBoy: done, wait again.");
                    } finally {
                        restaurant.waitPerson.lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("BusBoy is killed.");
            }
        }
    }

    class WaitPerson implements Runnable {
        private Restaurant restaurant;
        protected ReentrantLock lock = new ReentrantLock();
        protected Condition condition = lock.newCondition();

        WaitPerson(Restaurant restaurant) {
            this.restaurant = restaurant;
        }
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // wait for chef
//                    synchronized (restaurant.chef) {
//                        while (restaurant.meal == null) {
//                            System.out.println("WaitPerson: wait meal to be ready.");
//                            restaurant.chef.wait();
//                            System.out.println("WaitPerson: get meal! take to guest");
//                        }
//                    }
                    restaurant.chef.lock.lock();
                    try {
                        System.out.println("WaitPerson: wait meal to be ready.");
                        restaurant.chef.condition.await();
                        System.out.println("WaitPerson: get meal! take to guest");
                    } finally {
                        restaurant.chef.lock.unlock();
                    }

                    //take the meal to guest.
                    TimeUnit.SECONDS.sleep(1);
                    restaurant.meal = null;
                    restaurant.busBoy.isCleaned = false;

                    //notify the chef and busboy(waitperson is ready)
//                    synchronized (this) {
//                        System.out.println("WaitPerson: I am ready.");
//                        notifyAll();
//                    }
                    lock.lock();
                    try {
                        condition.signalAll();
                    }finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("WaitPerson: wait person is killed.");
            }
        }
    }

    class Chef implements Runnable {
        private Restaurant restaurant;
        private int count = 3;
        protected ReentrantLock lock = new ReentrantLock();
        protected Condition condition = lock.newCondition();

        Chef(Restaurant restaurant) {
            this.restaurant = restaurant;
        }
        public void run() {
            try {
                while (!Thread.interrupted()) {
                    // wait for waitperson to take meal
//                    synchronized (restaurant.waitPerson) {
//                        while (restaurant.meal != null) {
//                            System.out.println("Chef: wait for waitperson to take meal.");
//                            restaurant.waitPerson.wait();
//                            System.out.println("Chef: waitperson have taken meal.");
//                        }
//                    }
                    restaurant.waitPerson.lock.lock();
                    try {
                        while (restaurant.meal != null) {
                            System.out.println("Chef: wait for waitperson to take meal.");
                            restaurant.waitPerson.condition.await();
                            System.out.println("Chef: waitperson have taken meal.");
                        }
                    } finally {
                        restaurant.waitPerson.lock.unlock();
                    }

                    //do next meal and notify waitperson(chef is ready)
                    if (count == 0) {
                        System.out.println("Chef: out of food, Closed.");
                        restaurant.service.shutdownNow();
                        return;   // this is new.
                    }
                    System.out.println("Chef: making food...");
                    TimeUnit.SECONDS.sleep(2);
                    restaurant.meal = new Meal();
                    count--;

//                    synchronized (this) {
//                        System.out.println("Chef: notify waitperson to take meal.");
//                        notify();
//                    }
                    lock.lock();
                    try {
                        System.out.println("Chef: notify waitperson to take meal.");
                        condition.signalAll();
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (InterruptedException e) {
                System.out.println("Chef: chef is killed.");
            }
        }
    }


}

