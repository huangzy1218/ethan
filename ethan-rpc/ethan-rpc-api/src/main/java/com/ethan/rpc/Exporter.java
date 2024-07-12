package com.ethan.rpc;

/**
 * Exporter (API/SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface Exporter<T> {

    // todo

    /**
     * get invoker.
     *
     * @return invoker
     */
    Invoker<T> getInvoker();
    
}
