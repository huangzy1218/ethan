package com.ethan.remoting;

/**
 * Remoting client.
 *
 * @author Huang Z.Y.
 */
public interface RemotingClient {

    /**
     * Open client.
     */
    default void open() {
    }

    /**
     * Connect to server.
     */
    void connect() throws Throwable;

    /**
     * Disconnect to server,
     */
    default void close() throws Throwable {
    }

}
