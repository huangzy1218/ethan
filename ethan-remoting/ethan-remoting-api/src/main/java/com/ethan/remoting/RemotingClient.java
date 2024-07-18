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
    void open();

    /**
     * Connect to server.
     */
    void connect() throws Throwable;

    /**
     * Disconnect to server,
     */
    void close() throws Throwable;

    /**
     * Get the connected channel.
     *
     * @return Channel
     */
    Channel getChannel();

}
