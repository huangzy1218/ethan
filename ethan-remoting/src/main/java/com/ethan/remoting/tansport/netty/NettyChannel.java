package com.ethan.remoting.tansport.netty;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.util.PayloadDropper;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.channel.ChannelFuture;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Netty Channel, a encapsulation for {@link org.jboss.netty.channel.Channel}.
 *
 * @author Huang Z.Y.
 * @see org.jboss.netty.channel.Channel
 */
@Slf4j
public class NettyChannel implements Channel {

    private volatile boolean closed;

    private static final ConcurrentMap<org.jboss.netty.channel.Channel, NettyChannel> CHANNEL_MAP =
            new ConcurrentHashMap<>();

    private final org.jboss.netty.channel.Channel channel;
    private final URL url;


    private NettyChannel(org.jboss.netty.channel.Channel channel, URL url) {
        this.channel = channel;
        this.url = url;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void send(Object message) throws RemotingException {
        if (isClosed()) {
            throw new RemotingException(this, "Failed to send message "
                    + (message == null ? "" : message.getClass().getName()));
        }

        boolean success = true;
        int timeout = 0;
        try {
            ChannelFuture future = channel.write(message);
            Throwable cause = future.getCause();
            if (cause != null) {
                throw cause;
            }
        } catch (Throwable e) {
            throw new RemotingException(
                    this, "Failed to send message " + PayloadDropper.getRequestWithoutData(message) + " to "
                    + getRemoteAddress() + ", cause: " + e.getMessage(),
                    e);
        }

        if (!success) {
            throw new RemotingException(this,
                    "Failed to send message " + PayloadDropper.getRequestWithoutData(message) + " to "
                            + getRemoteAddress() + "in timeout(" + timeout + "ms) limit");
        }
    }

    @Override
    public void close() {
        closed = true;
        removeChannelIfDisconnected(channel);
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    static void removeChannelIfDisconnected(Channel ch) {
        if (ch != null && !ch.isConnected()) {
            CHANNEL_MAP.remove(ch);
        }
    }

    public static NettyChannel getOrAddChannel(io.netty.channel.Channel ch, URL url) {
        if (ch == null) {
            return null;
        }
        NettyChannel ret = CHANNEL_MAP.get(ch);
        if (ret == null) {
            NettyChannel nc = new NettyChannel(ch, url);
            if (ch.isActive()) {
                ret = CHANNEL_MAP.putIfAbsent(ch, nc);
            }
            if (ret == null) {
                ret = nc;
            }
        }
        return ret;
    }

}
