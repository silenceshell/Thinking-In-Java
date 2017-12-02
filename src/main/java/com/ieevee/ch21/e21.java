package com.ieevee.ch21;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * exercise 21
 */
public class e21 {
    public static void main(String args[]) {
        Integer x = 1;
        ExecutorService service = Executors.newCachedThreadPool();

        BaseClass baseClass = new BaseClass();
        ExtendClass extendClass = new ExtendClass(baseClass);
        service.execute(baseClass);
        service.execute(extendClass);

        service.shutdown();
        try {
            boolean ret = service.awaitTermination(10, TimeUnit.SECONDS);
            System.out.println(ret);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class BaseClass implements Runnable {
    BaseClass() {
    }
    synchronized void printMessage() {
        try {
            wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("catch the message!");
    }
    synchronized void allowPrint() {
        notify();
    }
    public void run() {
        this.printMessage();
        System.out.println("exit now!");
    }
}

class ExtendClass extends BaseClass {
    private final BaseClass baseClass;
    ExtendClass(BaseClass baseClass) {
        this.baseClass = baseClass;
    }
    @Override
    public void run() {
        try {
            System.out.println("wait for 3 seconds.");
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            System.out.println("interrupt.");
        }
        baseClass.allowPrint();
    }
}