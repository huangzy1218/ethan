package com.ethan.remoting;

/**
 * Remoting Server (API/SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface RemotingServer {

    void doOpen();

    void doClose();

}
