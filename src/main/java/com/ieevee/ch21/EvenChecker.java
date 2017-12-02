package com.ieevee.ch21;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EvenChecker implements Runnable {
    private IntGenerater intGenerater;
    private int id;
    public EvenChecker(IntGenerater intGenerater, int id) {
        this.intGenerater = intGenerater;
        this.id = id;
    }
    public void run() {
        while (!this.intGenerater.isCanceled()) {
            if ((this.intGenerater.next() % 2) != 0) {
                System.out.println("generater error, id:" + this.id);
                this.intGenerater.cancel();
            }
        }
    }

    public static void test(IntGenerater intGenerater, int number) {
        ExecutorService executorService = Executors.newCachedThreadPool();
        for (int i = 0; i< number; i++) {
            executorService.execute(new EvenChecker(intGenerater, i));
        }
        executorService.shutdown();
    }
    public static void test(IntGenerater intGenerater) {
        test(intGenerater, 10);
    }
}
