package com.ieevee.ch21;

/**
 * fat man
 */
public class Fat {
    private volatile double d;
    private static int count;
    private final int id = count++;
    Fat() {
        for (int i=0; i< 100000; i++) {
            d += (Math.PI + Math.E)/(double)i;
        }
    }

    private void operation() {
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "Fat id:" + id;
    }
}
