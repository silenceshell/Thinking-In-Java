package com.ieevee.ch21;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * restaurant !
 */
public class Restaurant {
    Meal meal = null;
    WaitPerson waitPerson = new WaitPerson(this);
    Chef chef = new Chef(this);
    ExecutorService service = Executors.newCachedThreadPool();

    Restaurant() {
        service.execute(chef);
        service.execute(waitPerson);
    }

    public static void main(String[] args) {
        new Restaurant();
    }
}

class Meal {

}

class WaitPerson implements Runnable {
    private Restaurant restaurant;
    WaitPerson(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // wait for chef
                synchronized (restaurant.chef) {
                    while (restaurant.meal == null) {
                        System.out.println("WaitPerson: wait meal to be ready.");
                        restaurant.chef.wait();
                        System.out.println("WaitPerson: get meal! take to guest");
                    }
                }
                //take the meal to guest.
                TimeUnit.SECONDS.sleep(1);
                restaurant.meal = null;

                //notify the chef(waitperson is ready)
                synchronized (this) {
                    System.out.println("WaitPerson: I am ready.");
                    notify();
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
    Chef(Restaurant restaurant) {
        this.restaurant = restaurant;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                // wait for waitperson to take meal
                synchronized (restaurant.waitPerson) {
                    while (restaurant.meal != null) {
                        System.out.println("Chef: wait for waitperson to take meal.");
                        restaurant.waitPerson.wait();
                        System.out.println("Chef: waitperson have taken meal.");
                    }
                }

                //do next meal and notify waitperson(chef is ready)
                if (count == 0) {
                    System.out.println("Chef: out of food, Closed.");
                    restaurant.service.shutdownNow();
                }
                System.out.println("Chef: making food...");
                TimeUnit.SECONDS.sleep(2);
                restaurant.meal = new Meal();
                count--;
                synchronized (this) {
                    System.out.println("Chef: notify waitperson to take meal.");
                    notify();
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Chef: chef is killed.");
        }
    }
}

