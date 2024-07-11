package com.ethan.remoting;

import java.util.concurrent.CompletableFuture;

/**
 * Remoting client.
 *
 * @author Huang Z.Y.
 */
public interface RemotingClient {

    /**
     * Open client.
     */
    void doOpen();

    /**
     * Connect to server.
     */
    void doConnect() throws Throwable;

    /**
     * Disconnect to server,
     */
    void doDisConnect() throws Throwable;

    /**
     * Get the connected channel.
     *
     * @return Channel
     */
    Channel getChannel();

    /**
     * Send request.
     */
    CompletableFuture<Object> request(Object request) throws RemotingException;

}
