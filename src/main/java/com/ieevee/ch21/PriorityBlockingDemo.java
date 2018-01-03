package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * priority blocking queue
 */
class PrioritizedTask implements Runnable, Comparable<PrioritizedTask>{
    private final int priority;
    private static int count;
    private final int id = count++;
    private Random random = new Random(47);
    private static List<PrioritizedTask> list = new ArrayList<PrioritizedTask>();

    PrioritizedTask() {
        this.priority = -1;
    }

    PrioritizedTask(int priority) {
        this.priority = priority;
        list.add(this);
    }

    public void run() {
        try {
            TimeUnit.MILLISECONDS.sleep(random.nextInt(250));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Task id " + this.id + " priority is " + this.priority;
    }

    public int compareTo(PrioritizedTask o) {
        return (this.priority > o.getPriority()) ? -1:
                ((this.priority < o.getPriority()) ? 1:0);
    }

    public int getPriority() {
        return priority;
    }

    static class EndSentinel extends PrioritizedTask {
        private ExecutorService service;
        EndSentinel(ExecutorService service) {
            this.service = service;
        }
        @Override
        public void run() {
            int cnt = 0;
            System.out.println("---------------------");
            for (PrioritizedTask task : list) {
                if (cnt % 5 == 0) {
                    System.out.println();
                }
                System.out.printf("%s  ", task);
                cnt++;
            }

            this.service.shutdownNow();
        }
    }
}

class QueueProducer implements Runnable{
    private PriorityBlockingQueue<Runnable> queue;
    private ExecutorService service;
    private Random random = new Random(47);
    QueueProducer(PriorityBlockingQueue<Runnable> queue, ExecutorService service) {
        this.queue = queue;
        this.service = service;
    }

    public void run() {
        for (int i=0; i< 10; i++) {
            queue.add(new PrioritizedTask(random.nextInt(10)));
            Thread.yield();
        }

        try {
            for (int i=0; i< 10; i++) {
                TimeUnit.MILLISECONDS.sleep(250);
                queue.add(new PrioritizedTask(10));
            }

            for (int i=0; i< 10; i++) {
                queue.add(new PrioritizedTask(i));
            }

            queue.add(new PrioritizedTask.EndSentinel(this.service));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class QueueConsumer implements Runnable{
    private PriorityBlockingQueue<Runnable> queue;
    QueueConsumer(PriorityBlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                queue.take().run();
            }
        } catch (InterruptedException e ) {
            e.printStackTrace();
        }
    }
}

public class PriorityBlockingDemo {
    public static void main(String args[]) {
        ExecutorService exec = Executors.newCachedThreadPool();
        PriorityBlockingQueue<Runnable> queue = new PriorityBlockingQueue<Runnable>();

        exec.execute(new QueueProducer(queue, exec));
        exec.execute(new QueueConsumer(queue));

    }
}