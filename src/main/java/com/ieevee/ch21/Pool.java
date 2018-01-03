package com.ieevee.ch21;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

/**
 * pool
 */
public class Pool<T> {
    private int size;
    private boolean[] checkedOut;
    private Semaphore semaphore;
    private List<T> items = new ArrayList<T>();

    Pool(Class<T> classObject, int size) {
        this.size = size;
        this.checkedOut = new boolean[size];
        this.semaphore = new Semaphore(size, true);
        try {
            for (int i=0; i<size; i++) {
                items.add(classObject.newInstance());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    T checkOut() {
        try {
            semaphore.acquire();
            return getItem();
        }catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }
    private synchronized T getItem() {
        for (int i=0; i<size; i++) {
            if (!checkedOut[i]) {
                checkedOut[i] = true;
                return items.get(i);
            }
        }
        System.out.println("!! error");
        return null;
    }

    void checkIn(T item) {
        if (putItem(item)) {
            semaphore.release();
        }
    }

    private synchronized boolean putItem(T item) {
        int index = items.indexOf(item);
        if (index == -1) {
            return false;
        }
        checkedOut[index] = false;
        return true;
    }
}
