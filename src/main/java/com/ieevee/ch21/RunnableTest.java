package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.concurrent.*;

public class RunnableTest implements Runnable {
    private int i=1;
    private int priority;
    public RunnableTest(int priority) {
        this();
        this.priority = priority;
    }
    public RunnableTest() {
        System.out.println("RunnableTest start.");
    }
    @Override
    public String toString() {
        return Thread.currentThread() + ":" + Thread.currentThread().getPriority();
    }
    public void run() {
//        Thread.currentThread().setPriority(this.priority);
        synchronized (this) {
            int j=0;
            for (; j<3; j++) {
//                System.out.println(i++);
//                System.out.println(this);
//                Thread.yield();
                try {
                    TimeUnit.MILLISECONDS.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            Thread thread = new ThreadTest();
//            thread.start();
//            System.out.println("thread is daemon?:" + thread.isDaemon());
//            System.out.println("RunnableTest end.");
        }
        throw new RuntimeException();
    }

    public static void main(String[] argcs) {

        Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler());
        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadDaemonFactory());
//        ExecutorService executorService = Executors.newCachedThreadPool(new ThreadExceptionHandlerFactory());

//        ExecutorService executorService = Executors.newFixedThreadPool(10);
        ArrayList<Future<String>> futures = new ArrayList<Future<String>>();
        for (int i=0; i< 2; i++) {
//            futures.add(executorService.submit(new CallableTest(i)));
            executorService.execute(new RunnableTest());

//            executorService.execute(new RunnableTest(Thread.MIN_PRIORITY));
//            Thread thread = new Thread(new RunnableTest());
//            thread.setDaemon(true);
//            thread.start();
        }
//        executorService.shutdown();

//        Thread thread1 = new ThreadTest();
//        Jointer jointer1 = new Jointer(thread1);
////        try {
////            thread1.join();
////        }catch (InterruptedException e) {
////            e.printStackTrace();
////        }
//        Thread thread2 = new ThreadTest();
//        Jointer jointer2 = new Jointer(thread2);
//
//        thread1.interrupt();

        System.out.println("xxxx");
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }



//        ThreadMethod threadMethod = new ThreadMethod();
//        threadMethod.runTask();
//
//        try {
//            TimeUnit.MILLISECONDS.sleep(1750);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        executorService.execute(new RunnableTest(Thread.MAX_PRIORITY));

//        for (Future<String> future : futures) {
//            try {
//                System.out.println(future.get());
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }
//        executorService.shutdown();
//        System.out.println("xxx1");

//        RunnableTest runnableTest = new RunnableTest();
//        new Thread(runnableTest).start();
//        System.out.println("xxx1");
//        new Thread(runnableTest).start();
//        System.out.println("xxx2");
//        new Thread(runnableTest).start();
//        System.out.println("xxx3");

//        ThreadTest threadTest1 = new ThreadTest();
//        ThreadTest threadTest2 = new ThreadTest();
//        ThreadTest threadTest3 = new ThreadTest();
//        threadTest1.start();
//        threadTest2.start();
//        threadTest3.start();
    }
}

class Jointer extends Thread {
    private Thread sleeper;
    public Jointer(Thread sleeper) {
        this.sleeper = sleeper;
        this.start();
    }
    public void run() {
        System.out.println("joiner started");
        try {
            this.sleeper.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("joiner finished");
    }
}

class ThreadMethod {
    public void runTask() {
        new Thread() {
            public void run() {
                System.out.println("xxxx");
            }
        }.start();
    }
}

class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    public void uncaughtException(Thread t, Throwable e) {
        System.out.println("uncaught Exception" + e.getMessage());
    }
}

class ThreadExceptionHandlerFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setUncaughtExceptionHandler(new UncaughtExceptionHandler());
        return thread;
    }
}

class ThreadDaemonFactory implements ThreadFactory {
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        return thread;
    }
}

class CallableTest implements Callable<String> {
    private int x = 0;
    CallableTest(int i) {
        x = i;
    }
    public String call() throws Exception {
        return "result is: " + x++;
    }
}

class ThreadTest extends Thread {
    private int i = 0;
//    @Override
    ThreadTest() {
        this.start();
    }
    public void run() {
        int i = 0;
        while (i<3) {
            try {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(Thread.currentThread() + "sleep for 1 second...");
            } catch (InterruptedException e) {
                System.out.println("interrupted?:" + Thread.currentThread().isInterrupted());
                break;
//                e.printStackTrace();
            }
            i++;
            Thread.yield();
        }
        throw new RuntimeException();
//        super.run();
//        System.out.println(i++);
    }
}
