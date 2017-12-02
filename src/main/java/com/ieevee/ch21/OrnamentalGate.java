package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ornamental gate
 */
public class OrnamentalGate {
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i=0; i < 10; i++) {
            executorService.execute(new Entrance());
        }
        executorService.shutdown();

        try {
            TimeUnit.MILLISECONDS.sleep(1666);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        Entrance.setCanceled(true);
        try {
            if (!executorService.awaitTermination(120, TimeUnit.MILLISECONDS)) {
                System.out.println("wait tasks to finish failed.");
                System.exit(-1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Entrance.getSum());

    }
}

class Count {
    private int count;
    private Random random = new Random(47);
    Count() {
    }
    synchronized void increment() {
        int tmp = count;
        if (random.nextBoolean()) {
            Thread.yield();
        }
        count = tmp+1;
    }
    synchronized int getCount() {
        return count;
    }
}

class Entrance implements Runnable {
    private int sum;
    private static volatile boolean canceled;
    private static Count count = new Count();
    private static List<Entrance> entrances = new ArrayList<Entrance>();
    Entrance() {
        entrances.add(this);
    }

    static void setCanceled(boolean canceled) {
        Entrance.canceled = canceled;
    }

    public void run() {
        while (!canceled) {
            synchronized(this) {
                sum++;
            }
            count.increment();

            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

    public static int getCount() {
        return count.getCount();
    }

    public static int getSum() {
        int sum = 0;
        for (Entrance entrance: entrances) {
            sum+= entrance.sum;
        }
        return sum;
    }
}
