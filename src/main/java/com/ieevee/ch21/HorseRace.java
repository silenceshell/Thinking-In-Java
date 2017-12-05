package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

/**
 * horse Race use CyclicBarrier
 */
public class HorseRace {
    final List<Horse> horses = new ArrayList<Horse>();
    final ExecutorService service = Executors.newCachedThreadPool();

    HorseRace(int nHorse) {
        CyclicBarrier barrier = new CyclicBarrier(nHorse, new Runnable() {
            final static int FINISH_LINE = 15;
            public void run() {
                StringBuilder builder = new StringBuilder();
                for (int i=0; i<FINISH_LINE; i++) {
                    builder.append("#");
                }
                System.out.println(builder.toString());
                int maxStrides = 0;
                for (Horse horse : horses) {
                    if (maxStrides < horse.getStrides()) {
                        maxStrides = horse.getStrides();
                    }
                    StringBuilder builder2 = new StringBuilder();
                    for (int i=0; i<horse.getStrides(); i++) {
                        builder2.append("*");
                    }
                    if (horse.getStrides() >= FINISH_LINE) {
                        service.shutdownNow();
                    }
                    System.out.println(builder2.toString());
                }
            }
        });

        for (int i=0; i<5; i++) {
            Horse horse = new Horse(barrier);
            horses.add(horse);
            service.execute(horse);
        }
    }

    public static void main(String[] args) {
        new HorseRace(5);
    }
}

class Horse implements Runnable {
    private int strides = 0;
    private CyclicBarrier barrier;
    private static Random random = new Random(47);

    Horse(CyclicBarrier barrier) {
        this.barrier = barrier;
    }

    int getStrides() {
        return strides;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                TimeUnit.MILLISECONDS.sleep(random.nextInt(1000));
                this.strides += random.nextInt(3);
                barrier.await();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
