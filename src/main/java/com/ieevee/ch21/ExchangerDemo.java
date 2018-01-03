package com.ieevee.ch21;

import java.util.List;
import java.util.concurrent.*;

/**
 * exchanger demo
 */
class ExchangerProducer<T> implements Runnable{
    private List<T> list;
    private Generator<T> generator;
    private Exchanger<List<T>> exchanger;
    private Class<T> type;
    ExchangerProducer(Exchanger<List<T>> exchanger, List<T> list, Generator<T> generator) {
        this.list = list;
        this.exchanger = exchanger;
        this.generator = generator;
    }
    ExchangerProducer(Exchanger<List<T>> exchanger, List<T> list, Class<T> type) {
        this.list = list;
        this.exchanger = exchanger;
        this.type = type;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                for (int i=0; i<3; i++) {
                    if (null != type) {
                        list.add(type.newInstance());
                    }
                    if (null != generator) {
                        list.add(generator.next());
                    }
                }
                list = exchanger.exchange(list);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            System.out.println("producer exit.");
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
    }
}

class ExchangerConsumer<T> implements Runnable{
    private List<T> list;
    private Exchanger<List<T>> exchanger;
    ExchangerConsumer(Exchanger<List<T>> exchanger, List<T> list) {
        this.list = list;
        this.exchanger = exchanger;
    }
    public void run() {
        try {
            while (!Thread.interrupted()) {
                System.out.println("wait for exchange");
                list = exchanger.exchange(list);
                for (T t: list) {
                    System.out.println(t);
                    list.remove(t);
                }
            }
        } catch (InterruptedException e ) {
            System.out.println("consumer exit.");
        }
    }
}

public class ExchangerDemo {

    public static void main(String args[]) throws InterruptedException {
        Exchanger<List<Fat>> exchanger = new Exchanger<List<Fat>>();
        ExecutorService service = Executors.newCachedThreadPool();

        List<Fat> pList = new CopyOnWriteArrayList<Fat>();
        List<Fat> cList = new CopyOnWriteArrayList<Fat>();

//        service.execute(new ExchangerProducer<Fat>(exchanger, pList, BasicGenerator.create(Fat.class)));
        service.execute(new ExchangerProducer<Fat>(exchanger, pList, Fat.class));
        service.execute(new ExchangerConsumer<Fat>(exchanger, cList));

        TimeUnit.SECONDS.sleep(3);
        service.shutdownNow();
    }
}
