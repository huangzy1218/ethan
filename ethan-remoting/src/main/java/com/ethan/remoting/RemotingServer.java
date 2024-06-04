package com.ethan.remoting;

import java.util.Collection;

/**
 * Remoting Server (API/SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface RemotingServer {

    void doOpen();

    void doClose();

    Collection<Channel> getChannels();
    
}
