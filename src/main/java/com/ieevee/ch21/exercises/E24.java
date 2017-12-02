package com.ieevee.ch21.exercises;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * exercise 24
 */
public class E24 {
    public static void main(String[] args) {
        new Market();
    }
}

class Item {
    private int seq;
    Item(int seq) {
        this.seq = seq;
    }
    int getItem() {
        return seq;
    }
}

class Market {
    Queue<Item> storage = new LinkedList<Item>();
    ExecutorService service = Executors.newCachedThreadPool();
    Producer producer = new Producer(this);
    Consumer consumer = new Consumer(this);
    Market() {
        service.execute(producer);
        service.execute(consumer);
    }
}

class Producer implements Runnable {
    private Market market;
    private int count = 1;
    Producer(Market market) {
        this.market = market;
    }
    public void run() {
        try {
            while (count <= 100) {
                //if storage is not full(10 max), produce.
                Item item = new Item(count++);
                market.storage.offer(item);
                synchronized (market.consumer) {
                    market.consumer.notifyAll();
                }

                synchronized (this) {
                    while (market.storage.size() >= 10) {
                        wait();
                    }
                }
            }
            market.service.shutdownNow();
            System.out.printf("producer is stopped produced %d items.\r\n", count-1);
        }catch (InterruptedException e) {
            System.out.printf("producer is killed, produced %d items.\r\n", count);
        }
    }
}

class Consumer implements Runnable{
    private Market market;
    List<Item> cart = new ArrayList<Item>();
    Consumer(Market market) {
        this.market = market;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                synchronized (this) {
                    // if storage is empty, wait.
                    while (market.storage.size() == 0) {
                        wait();
                    }
                }
                Item item = market.storage.poll();
                cart.add(item);
                System.out.println(item.getItem());
                synchronized (market.producer) {
                    market.producer.notifyAll();
                }
            }
        } catch (InterruptedException e) {
            System.out.printf("consumer is killed, got %d items.\r\n", cart.size());
        }

    }
}
