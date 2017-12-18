package com.ieevee.ch21;

import java.util.Random;
import java.util.concurrent.*;

/**
 * trigger queue demos
 */

class DelayTask implements Delayed, Runnable{
    private static int count;
    private int id;
    private long trigger;
    private int delta;

    DelayTask() {
    }

    DelayTask(int delayMS) {
        this.id = count++;
        this.delta = delayMS;
        this.trigger = System.nanoTime() + TimeUnit.NANOSECONDS.convert(delta, TimeUnit.MILLISECONDS);
    }

    public long getDelay(TimeUnit unit) {
        return unit.convert(trigger - System.nanoTime(), TimeUnit.NANOSECONDS);
    }

    public int compareTo(Delayed o) {
        DelayTask task = (DelayTask)o;
        if (this.trigger < task.trigger) {
            return -1;
        } else if (this.trigger > task.trigger) {
            return 1;
        }
        return 0;
    }

    public void run() {
        System.out.printf("id %d delta %d\r\n", id, delta);
    }

    public static class EndSentinel extends DelayTask{
        ExecutorService service;

        EndSentinel(int endTime, ExecutorService service) {
            super(endTime);
            this.service = service;
        }

        @Override
        public void run() {
            System.out.println("all done.");
            service.shutdownNow();
        }
    }
}

class DelayQueueConsumer implements Runnable{
    DelayQueue<DelayTask> tasks;
    DelayQueueConsumer(DelayQueue<DelayTask> tasks) {
        this.tasks = tasks;
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                tasks.take().run();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

public class DelayQueueDemo {

    public static void main(String[] args) {
        DelayQueue<DelayTask> tasks = new DelayQueue<DelayTask>();
        Random random = new Random(47);
        ExecutorService service = Executors.newCachedThreadPool();

        for (int i=0; i<5; i++) {
            tasks.add(new DelayTask(random.nextInt(5000)));
        }
        tasks.put(new DelayTask.EndSentinel(5000, service));

        service.execute(new DelayQueueConsumer(tasks));
    }


}
