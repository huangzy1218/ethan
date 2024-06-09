package com.ethan.registry;

import com.ethan.common.URL;

/**
 * Registry (SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface Registry {

    /**
     * Register data, such as provider service, consumer address.
     *
     * @param url Registration information, such as ethan://127.0.0.1/com.example.Greeting?version=1.0.0
     */
    void register(URL url);

    /**
     * Unregister.
     *
     * @param url Registration information, such as ethan://127.0.0.1/com.example.Greeting?version=1.0.0
     */
    void unregister(URL url);

    /**
     * Subscribe to eligible registered data and react when data is changed.
     *
     * @param url      Subscription condition
     * @param listener A listener of the change event
     */
    void subscribe(URL url, NotifyListener listener);

    /**
     * Unsubscribe.
     *
     * @param url      Subscription condition
     * @param listener A listener of the change event
     */
    void unsubscribe(URL url, NotifyListener listener);

    /**
     * Query the registered data that matches the conditions.
     *
     * @param url Query condition
     * @return The registered information list
     */
    com.ethan.common.URL lookup(URL url);

}
