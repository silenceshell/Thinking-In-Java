package com.ieevee.ch21;

/**
 * semaphore demo
 */
public class SemaphoreDemo {
    private Pool pool;
    private int size;
    class CheckoutTask implements Runnable{
        public void run() {
            for
        }
    }

    SemaphoreDemo(int size) {
        this.size = size;
        this.pool = new Pool(Fat.class, size);
    }

    public static void main(String args[]) {
        SemaphoreDemo demo = new SemaphoreDemo(10);
    }
}
