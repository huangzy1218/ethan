package com.ethan.registry;

import com.ethan.common.Node;
import com.ethan.common.URL;

import java.util.List;

/**
 * Registry (SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface Registry extends Node {

    /**
     * Init the registry.
     */
    void init();

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
     * Subscribe with listener.
     *
     * @param url      Subscription information
     * @param listener NotifyListener
     */
    void subscribe(URL url, NotifyListener listener);

    /**
     * Unsubscribe the listener.
     *
     * @param url      Subscription information
     * @param listener NotifyListener
     */
    void unsubscribe(URL url, NotifyListener listener);

    /**
     * Query the registered data that matches the conditions.
     *
     * @param url Query condition
     * @return The registered information list
     */
    List<URL> lookup(URL url);

    /**
     * Server heartbeat.
     */
    void heartbeat();
}
