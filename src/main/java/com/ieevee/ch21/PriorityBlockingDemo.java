package com.ieevee.ch21;

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
    PrioritizedTask() {
        this.priority = 0;
    }
    PrioritizedTask(int priority) {
        this.priority = priority;
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

    class EndSentinel extends PrioritizedTask {
        EndSentinel(int priority) {
            super(priority);
        }
        @Override
        public void run() {
            super.run();
        }
    }
}

class QueueProducer implements Runnable{
    private PriorityBlockingQueue<Runnable> queue;
    private Random random = new Random(47);
    QueueProducer(PriorityBlockingQueue<Runnable> queue) {
        this.queue = queue;
    }

    public void run() {
        for (int i=0; i< 10; i++) {
            queue.add(new PrioritizedTask(random.nextInt(100)));
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
            for (int i=0; i< 10; i++) {
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

        exec.execute(new QueueProducer(queue));
        exec.execute(new QueueConsumer(queue));

    }
}