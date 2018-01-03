package com.ieevee.ch21;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * green house scheduler
 */
public class GreenHouseScheduler {
    private ScheduledThreadPoolExecutor service = new ScheduledThreadPoolExecutor(10);

    private class LightOn implements Runnable{
        public void run() {
            System.out.println("light on!");
        }
    }
    private class LightOff implements Runnable{
        public void run() {
            System.out.println("light off!");
        }
    }
    private class WaterOn implements Runnable{
        public void run() {
            System.out.println("water on!");
        }
    }
    private class WaterOff implements Runnable{
        public void run() {
            System.out.println("water off!");
        }
    }
    private class Terminal implements Runnable{
        private ExecutorService service;
        Terminal(ExecutorService service) {
            this.service = service;
        }

        public void run() {
            service.shutdownNow();
        }
    }

    private void schedule(Runnable instance, long period) {
        this.service.schedule(instance, period, TimeUnit.MILLISECONDS);
    }

    private void repeat(Runnable instance, long initial, long period) {
        this.service.scheduleAtFixedRate(instance, initial, period, TimeUnit.MILLISECONDS);
    }

    public static void main(String args[]) {
        GreenHouseScheduler scheduler = new GreenHouseScheduler();
        Terminal terminal = scheduler.new Terminal(scheduler.service);
        scheduler.schedule(terminal, 3000);
        scheduler.repeat(scheduler.new LightOn(), 0, 400);
        scheduler.repeat(scheduler.new LightOff(), 200, 400);
        scheduler.repeat(scheduler.new WaterOn(), 0, 400);
        scheduler.repeat(scheduler.new WaterOff(), 200, 400);
    }
}
