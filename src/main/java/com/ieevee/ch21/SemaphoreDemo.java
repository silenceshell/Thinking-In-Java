package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * semaphore demo
 */
class CheckoutTask<T> implements Runnable{
    private Pool<Fat> pool;
    CheckoutTask(Pool pool) {
        this.pool = pool;
    }
    public void run() {
        Fat fat = pool.checkOut();
        System.out.println("exec thread: fat id " + fat.toString() + " checkout");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
        pool.checkIn(fat);
        System.out.println("exec thread: fat id " + fat.toString() + " checkin");
    }
}
public class SemaphoreDemo {
    SemaphoreDemo() {
    }

    public static void main(String args[]) {
        final int size=10;
        List<Fat> list = new ArrayList<Fat>();
        final Pool<Fat> pool = new Pool(Fat.class, size);
        SemaphoreDemo demo = new SemaphoreDemo();
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i=0; i<size; i++) {
            service.execute(new CheckoutTask<Fat>(pool));
        }

        for (int i=0; i<size; i++) {
            Fat fat = pool.checkOut();
            System.out.println("main thread: fat id " + fat.toString() + " checkout");
            list.add(fat);
        }

        Future future = service.submit(new Runnable() {
            public void run() {
                Fat fat = pool.checkOut();
                System.out.println("err!!");
                fat.operation();
            }
        });

        try {
            TimeUnit.SECONDS.sleep(2);
            future.cancel(true);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Fat fat: list) {
            pool.checkIn(fat);
        }

        for (Fat fat: list) {
            pool.checkIn(fat);
        }

        service.shutdownNow();
    }
}
