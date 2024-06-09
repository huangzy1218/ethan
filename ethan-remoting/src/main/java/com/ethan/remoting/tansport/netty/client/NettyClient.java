package com.ethan.remoting.tansport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.RemotingException;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;

import static com.ethan.remoting.tansport.netty.NettyEventLoopFactory.shouldEpoll;

/**
 * Netty client.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyClient implements RemotingClient {

    private Bootstrap bootstrap;

    private volatile URL url;

    private final EventLoopGroup eventLoopGroup;

    /**
     * Current channel. Each successful invocation of {@link NettyClient#doConnect()} will
     * replace this with new channel and close old channel.
     */
    private volatile Channel channel;

    public NettyClient(final URL url) {
        this.url = url;
        eventLoopGroup = new NioEventLoopGroup();
        doOpen();
    }

    @Override
    public void doOpen() {

    }

    protected void initBootstrap(NettyClientHandler nettyClientHandler) {
        bootstrap
                .group(eventLoopGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                //.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, getTimeout())
                .channel(socketChannelClass());
        // todo

        //bootstrap.option(ChannelOption.CONNECT_TIMEOUT_MILLIS, Math.max(DEFAULT_CONNECT_TIMEOUT, getConnectTimeout()));
//        SslContext sslContext = SslContexts.buildClientSslContext(getUrl());
//        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
//
//            @Override
//            protected void initChannel(SocketChannel ch) throws Exception {
//                int heartbeatInterval = UrlUtils.getHeartbeat(getUrl());
//
//                if (sslContext != null) {
//                    ch.pipeline().addLast("negotiation", new SslClientTlsHandler(sslContext));
//                }
//
//                NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl(), NettyClient.this);
//                ch.pipeline() // .addLast("logging",new LoggingHandler(LogLevel.INFO))//for debug
////                        .addLast("decoder", adapter.getDecoder())
////                        .addLast("encoder", adapter.getEncoder())
//                        // todo
//                        .addLast("client-idle-handler", new IdleStateHandler(heartbeatInterval, 0, 0, MILLISECONDS))
//                        .addLast("handler", nettyClientHandler);
//
//                String socksProxyHost =
//                        ConfigurationUtils.getProperty(getUrl().getOrDefaultApplicationModel(), SOCKS_PROXY_HOST);
//                if (socksProxyHost != null && !isFilteredAddress(getUrl().getHost())) {
//                    int socksProxyPort = Integer.parseInt(ConfigurationUtils.getProperty(
//                            getUrl().getOrDefaultApplicationModel(), SOCKS_PROXY_PORT, DEFAULT_SOCKS_PROXY_PORT));
//                    Socks5ProxyHandler socks5ProxyHandler =
//                            new Socks5ProxyHandler(new InetSocketAddress(socksProxyHost, socksProxyPort));
//                    ch.pipeline().addFirst(socks5ProxyHandler);
//                }
//            }
//        });
        // todo
    }

    @Override
    public void doConnect() throws Throwable {

    }

    @Override
    public void doDisConnect() throws Throwable {

    }

    @Override
    public Channel getChannel() {
        return null;
    }

    private void doConnect(InetSocketAddress serverAddress) throws RemotingException {
        ChannelFuture future = bootstrap.connect(serverAddress);

    }

    public static Class<? extends SocketChannel> socketChannelClass() {
        return shouldEpoll() ? EpollSocketChannel.class : NioSocketChannel.class;
    }

}
