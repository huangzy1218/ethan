package com.ethan.remoting;

import com.ethan.common.RemotingException;

import java.net.InetSocketAddress;

/**
 * TimeoutException (API, Prototype, ThreadSafe)。
 *
 * @author Huang Z.Y.
 */
public class TimeoutException extends RemotingException {

    public static final int CLIENT_SIDE = 0;
    public static final int SERVER_SIDE = 1;
    private static final long serialVersionUID = 3122966731958222692L;
    private final int phase;

    public TimeoutException(boolean serverSide, String message) {
        super(message);
        this.phase = serverSide ? SERVER_SIDE : CLIENT_SIDE;
    }

    public TimeoutException(
            boolean serverSide, InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(localAddress, remoteAddress, message);
        this.phase = serverSide ? SERVER_SIDE : CLIENT_SIDE;
    }

    public int getPhase() {
        return phase;
    }

    public boolean isServerSide() {
        return phase == 1;
    }

    public boolean isClientSide() {
        return phase == 0;
    }

}
