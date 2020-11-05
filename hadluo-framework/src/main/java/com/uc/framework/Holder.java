package com.uc.framework;

public class Holder<T> {
    private T t;

    public Holder(T t) {
        this.t = t;
    }

    public Holder() {
    }

    public T get() {
        return t;
    }

    public void set(T t) {
        this.t = t;
    }

}
