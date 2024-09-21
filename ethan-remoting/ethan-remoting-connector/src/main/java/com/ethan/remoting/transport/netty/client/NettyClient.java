package com.ethan.remoting.transport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.support.DefaultFuture;
import com.ethan.remoting.transport.AbstractEndpoint;
import com.ethan.remoting.transport.netty.NettyChannel;
import com.ethan.remoting.transport.netty.codec.NettyCodecAdapter;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.CompletableFuture;

import static com.ethan.common.constant.CommonConstants.DEFAULT_CONNECT_TIMEOUT;
import static com.ethan.remoting.transport.netty.NettyEventLoopFactory.shouldEpoll;
import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 * Netty client.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyClient extends AbstractEndpoint implements RemotingClient {

    private static final String SOCKS_PROXY_HOST = "socksProxyHost";

    private static final String SOCKS_PROXY_PORT = "socksProxyPort";

    private static final String DEFAULT_SOCKS_PROXY_PORT = "1080";

    private static final String DEFAULT_SOCKS_HOST = "127.0.0.1";
    private final EventLoopGroup eventLoopGroup;
    private Bootstrap bootstrap;
    /**
     * Current channel. Each successful invocation of {@link NettyClient#doConnect()} will
     * replace this with new channel and close old channel.
     */
    private volatile io.netty.channel.Channel channel;

    public NettyClient(final URL url) {
        super(url);
        eventLoopGroup = new NioEventLoopGroup();
        doOpen();
        doConnect();
    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

    @Override
    public void doOpen() {
        final NettyClientHandler nettyClientHandler = new NettyClientHandler(getUrl());
        bootstrap = new Bootstrap();
        initBootstrap(nettyClientHandler);
    }

    protected void initBootstrap(NettyClientHandler nettyClientHandler) {
        bootstrap
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.max(DEFAULT_CONNECT_TIMEOUT, getConnectTimeout()))
                .channel(socketChannelClass());

        bootstrap.handler(new ChannelInitializer<SocketChannel>() {

            @Override
            protected void initChannel(SocketChannel ch) throws Exception {
                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl());
                ch.pipeline()
                        .addLast("decoder", adapter.getDecoder())
                        .addLast("encoder", adapter.getEncoder())
                        .addLast("handler", nettyClientHandler);
            }
        });
    }

    @Override
    public void doConnect() {
        InetSocketAddress connectAddress = new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
        doConnect(connectAddress);
    }

    @Override
    public void doDisConnect() throws Throwable {
        NettyChannel.removeChannelIfDisconnected(channel);
    }

    @Override
    public Channel getChannel() {
        io.netty.channel.Channel c = channel;
        if (c == null) {
            return null;
        }
        return NettyChannel.getOrAddChannel(c, getUrl());
    }

    @Override
    public CompletableFuture<Object> request(Object request) throws RemotingException {
        Request req;
        if (request instanceof Request) {
            req = (Request) request;
        } else {
            // Create request
            req = new Request();
            req.setData(request);
        }
        NettyChannel ch = NettyChannel.getOrAddChannel(channel, getUrl());
        DefaultFuture future = DefaultFuture.newFuture(ch, req, getConnectTimeout());
        try {
            ch.send(req);
        } catch (RemotingException e) {
            future.cancel(true);
            throw e;
        }
        return future;
    }

    private void doConnect(InetSocketAddress serverAddress) {
        ChannelFuture future = bootstrap.connect(serverAddress);
        boolean ret = future.awaitUninterruptibly(getConnectTimeout(), MILLISECONDS);
        if (ret && future.isSuccess()) {
            io.netty.channel.Channel newChannel = future.channel();
            io.netty.channel.Channel oldChannel = NettyClient.this.channel;
            NettyClient.this.channel = newChannel;
            if (oldChannel != null) {
                try {
                    log.info("Close old netty channel " + oldChannel + " on create new netty channel "
                            + newChannel);
                    oldChannel.close();
                } finally {
                    NettyChannel.removeChannelIfDisconnected(oldChannel);
                }
            }
        } else {
            // Log the cause of the failure
            Throwable cause = future.cause();
            if (cause != null) {
                log.error("Failed to connect to server: " + serverAddress, cause);
            } else {
                log.error("Failed to connect to server: " + serverAddress + " within timeout of " + getConnectTimeout() + " milliseconds.");
            }
        }
    }

    public void close() {
        try {
            doDisConnect();
        } catch (Throwable ignored) {
        }
    }

}
