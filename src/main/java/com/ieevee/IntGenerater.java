package com.ieevee;


public abstract class IntGenerater {
    private volatile boolean canceled = false;
    public abstract int next();
    public void cancel() {
        canceled = true;
    }
    public boolean isCanceled() {
        return this.canceled;
    }
}
