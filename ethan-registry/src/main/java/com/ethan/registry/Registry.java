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
     * Query the registered data that matches the conditions.
     *
     * @param url Query condition
     * @return The registered information list
     */
    URL lookup(URL url);

}
