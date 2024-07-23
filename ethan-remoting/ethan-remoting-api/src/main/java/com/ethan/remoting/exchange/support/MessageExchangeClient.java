package com.ethan.remoting.exchange.support;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.ExchangeClient;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.transport.netty.NettyChannel;
import com.ethan.remoting.transport.netty.client.NettyClient;

import java.net.InetSocketAddress;

/**
 * An encapsulation of {@link NettyChannel}.
 *
 * @author Huang Z.Y.
 */
public class MessageExchangeClient implements ExchangeClient {

    private final Channel channel;
    private NettyClient client;

    public MessageExchangeClient(io.netty.channel.Channel channel, URL url) {
        this.channel = new NettyChannel(channel, url);
        client = new NettyClient(url);
    }

    @Override
    public InetSocketAddress getLocalAddress() {
        return channel.getLocalAddress();
    }

    @Override
    public InetSocketAddress getRemoteAddress() {
        return channel.getRemoteAddress();
    }

    @Override
    public boolean isConnected() {
        return channel.isConnected();
    }

    @Override
    public void send(Object message) throws RemotingException {
        Request request;
        if (message instanceof Request) {
            request = (Request) message;
            client.send((Request) message);
        } else {
            request = new Request();
            request.setData(message);
        }
        client.send(request);
    }

    @Override
    public void close() {
        channel.close();
    }

    @Override
    public boolean isClosed() {
        return channel.isClosed();
    }

    @Override
    public Object getAttribute(String key) {
        return channel.getAttribute(key);
    }

    @Override
    public void setAttribute(String key, Object value) {
        channel.setAttribute(key, value);
    }

    @Override
    public URL getUrl() {
        return channel.getUrl();
    }

}
