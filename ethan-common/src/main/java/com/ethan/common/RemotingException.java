package com.ethan.common;

import lombok.Getter;

import java.net.InetSocketAddress;

/**
 * Remoting exception.
 *
 * @author Huang Z.Y.
 */
@Getter
public class RemotingException extends Exception {

    private InetSocketAddress localAddress;

    private InetSocketAddress remoteAddress;

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(String message) {
        super(message);
    }

    public RemotingException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public RemotingException(Exception e) {
        super(e);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause) {
        super(cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(
            InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message, Throwable cause) {
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }
}
