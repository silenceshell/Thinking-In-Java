package com.ieevee;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

/**
 * xx
 */
public class Interrupting {
    private static ExecutorService executorService;
    static void test(Runnable r) {
        Future future = executorService.submit(r);
        try {
            TimeUnit.MILLISECONDS.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        future.cancel(true);
    }

    public static void main(String[] args) {
        executorService = Executors.newCachedThreadPool();
        test(new SleepRunnable());
        InputStream inputStream = null;
        try {
            ServerSocket serverSocket = new ServerSocket(8080);
            Socket socket = new Socket("localhost", 8080);
            inputStream = socket.getInputStream();
            test(new IOBlock(inputStream));
        } catch (IOException e) {
            e.printStackTrace();
        }
        test(new IOBlock(System.in));

        executorService.shutdownNow();
        System.out.println("shutdown now.");
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e){
            e.printStackTrace();
        }

        try {
            System.in.close();
            if (inputStream != null) {
                inputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        final SynchronizedBlock block = new SynchronizedBlock();
//        new Thread() {
//            public void run() {
//                synchronized (block) {
//                    try {
//                        TimeUnit.MILLISECONDS.sleep(66666);
//                        System.out.println("finally sleep ended.");
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }.start();
//        test(block);
    }
}

class SynchronizedBlock implements Runnable {
    public void run() {
        synchronized (this) {
            System.out.println("synchronized block enter.");
        }
    }

}

class IOBlock implements Runnable {
    private InputStream is;
    IOBlock(InputStream in) {
        this.is = in;
    }
    public void run() {
        try {
            is.read();
        } catch (IOException e) {
            if (Thread.currentThread().interrupted()) {
                System.out.println("Thread is interrupted");
            } else {
                System.out.println("Thread is not interrupted.");
            }
        }
    }
}

class SleepRunnable implements Runnable {
    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(200);
        } catch (InterruptedException e) {
            if (Thread.currentThread().interrupted()) {
                System.out.println("Thread is interrupted.");
            } else {
                System.out.println("Thread is not interrupted.");
            }
        }
    }
}
