package com.ieevee.ch21;

import java.io.IOException;
import java.io.PipedReader;
import java.io.PipedWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * pipeWriter and pipeReader
 */
public class PipeIO {
    public static void main(String[] args) {
        Sender sender = new Sender();
        Receiver receiver = new Receiver(sender);

        ExecutorService service = Executors.newCachedThreadPool();
        service.execute(sender);
        service.execute(receiver);

        service.shutdown();
        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        service.shutdownNow();
    }
}

class Sender implements Runnable{
    private PipedWriter pipedWriter;
    PipedWriter getWriter() {
        return pipedWriter;
    }
    Sender() {
        this.pipedWriter = new PipedWriter();
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (char c = 'a'; c <='z'; c++) {
                    pipedWriter.write(c);
                    TimeUnit.MILLISECONDS.sleep(1000);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class Receiver implements Runnable{
    PipedReader pipedReader;
    Receiver(Sender sender) {
        try {
            pipedReader = new PipedReader(sender.getWriter());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("begin to read");
                int c = pipedReader.read();
                System.out.printf("read: %c \r\n", c);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}