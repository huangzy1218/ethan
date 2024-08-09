package com.ethan.remoting;

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

    public RemotingException(Channel channel, String msg) {
        this(
                channel == null ? null : channel.getLocalAddress(),
                channel == null ? null : channel.getRemoteAddress(),
                msg);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message) {
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Exception e) {
        super(e);
    }

    public RemotingException(Channel channel, Throwable cause) {
        this(
                channel == null ? null : channel.getLocalAddress(),
                channel == null ? null : channel.getRemoteAddress(),
                cause);
    }

    public RemotingException(InetSocketAddress localAddress, InetSocketAddress remoteAddress, Throwable cause) {
        super(cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, String message, Throwable cause) {
        this(
                channel == null ? null : channel.getLocalAddress(),
                channel == null ? null : channel.getRemoteAddress(),
                message,
                cause);
    }

    public RemotingException(
            InetSocketAddress localAddress, InetSocketAddress remoteAddress, String message, Throwable cause) {
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }
}
