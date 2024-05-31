package com.ethan.common.util;

/**
 * Helper Class for hold a value. This class can be used to safely share mutable data between threads.
 *
 * @author Huang Z.Y.
 */
public class Holder<T> {

    private volatile T value;

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }

}
