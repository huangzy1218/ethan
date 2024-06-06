package com.ethan.remoting;

import java.net.InetSocketAddress;

/**
 * Network communication Channel (API/SPI, Prototype, ThreadSafe).
 */
public interface Channel {


    /**
     * Get local address.
     *
     * @return Local address
     */
    InetSocketAddress getLocalAddress();

    /**
     * Get remote address.
     *
     * @return Remote address
     */
    InetSocketAddress getRemoteAddress();

    /**
     * Is connected.
     *
     * @return {@code true} for connected
     */
    boolean isConnected();

    /**
     * Send message.
     *
     * @param message Message to send
     */
    void send(Object message) throws RemotingException;


    /**
     * Close the channel.
     */
    void close();

    /**
     * Is closed.
     *
     * @return {@code true} for closed
     */
    boolean isClosed();

}
