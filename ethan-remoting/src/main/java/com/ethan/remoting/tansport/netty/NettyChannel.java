package com.ethan.remoting.tansport.netty;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.util.PayloadDropper;
import io.netty.channel.ChannelFuture;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Netty Channel, a encapsulation for {@link io.netty.channel.Channel}.
 *
 * @author Huang Z.Y.
 * @see org.jboss.netty.channel.Channel
 */
@Slf4j
public class NettyChannel implements Channel {

    private volatile boolean closed;


    private static final ConcurrentMap<io.netty.channel.Channel, NettyChannel> CHANNEL_MAP =
            new ConcurrentHashMap<>();

    private final io.netty.channel.Channel channel;
    private final URL url;


    private NettyChannel(io.netty.channel.Channel channel, URL url) {
        this.channel = channel;
        this.url = url;
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return (InetSocketAddress) channel.localAddress();
    }

    @Override
    public boolean isConnected() {
        return !closed;
    }

    @Override
    public void send(Object message) throws RemotingException {
        if (isClosed()) {
            throw new RemotingException(this, "Failed to send message "
                    + (message == null ? "" : message.getClass().getName()));
        }

        boolean success = true;
        try {
            ChannelFuture future = channel.write(message);
            Throwable cause = future.cause();
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
                            + getRemoteAddress());
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

    public Object getAttributeOrDefault(String key, Object defaultVal) {
        Object attribute = getAttribute(key);
        if (attribute == null) {
            attribute = defaultVal;
        }
        return attribute;

    }

    @Override
    public Object getAttribute(String key) {
        return url.getParameter(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        // The null value is not allowed in the ConcurrentHashMap
        if (value == null) {
            url.getAttributes().remove(key);
        } else {
            url.getAttributes().put(key, value);
        }
    }

    @Override
    public URL getUrl() {
        return url;
    }

    static void removeChannelIfDisconnected(io.netty.channel.Channel ch) {
        if (ch != null && !ch.isActive()) {
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
