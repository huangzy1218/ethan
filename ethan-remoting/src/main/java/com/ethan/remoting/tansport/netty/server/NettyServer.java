package com.ethan.remoting.tansport.netty.server;

import com.ethan.common.URL;
import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.NamedThreadFactory;
import com.ethan.common.util.RuntimeUtils;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.com.ethan.remoting.tansport.AbstractEndpoint;
import com.ethan.remoting.tansport.netty.NettyEventLoopFactory;
import com.ethan.remoting.tansport.netty.codec.NettyCodecAdapter;
import com.ethan.rpc.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static com.ethan.remoting.RemotingConstants.EVENT_LOOP_BOSS_POOL_NAME;
import static com.ethan.remoting.RemotingConstants.EVENT_LOOP_WORKER_POOL_NAME;
import static com.ethan.rpc.Constants.BIND_IP_KEY;
import static com.ethan.rpc.Constants.BIND_PORT_KEY;

/**
 * Netty server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServer extends AbstractEndpoint implements RemotingServer {

    private ServerBootstrap bootstrap;

    private io.netty.channel.Channel channel;

    private InetSocketAddress bindAddress;

    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public NettyServer(URL url) throws RemotingException {
        super(url);
        String bindIp = getUrl().getParameter(BIND_IP_KEY, getUrl().getHost());
        int bindPort = getUrl().getParameter(BIND_PORT_KEY, getUrl().getPort());
        bindAddress = new InetSocketAddress(bindIp, bindPort);
        doOpen();
    }

    @Override
    public void doOpen() {
        bootstrap = new ServerBootstrap();

        String bindIp = getUrl().getParameter(BIND_IP_KEY, getUrl().getHost());
        int bindPort = Integer.parseInt(getUrl().getParameter(BIND_PORT_KEY, String.valueOf(getUrl().getPort())));
        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getUrl());

        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, EVENT_LOOP_BOSS_POOL_NAME);
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, EVENT_LOOP_WORKER_POOL_NAME);

        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtils.cpus() * 2,
                new NamedThreadFactory("service-handler-group", false)
        );

        initServerBootstrap(nettyServerHandler);

        try {
            // The bond port is synchronized until the bond succeeds
            ChannelFuture future = bootstrap.bind(bindIp, bindPort).syncUninterruptibly();
            channel = future.channel();
        } catch (Throwable e) {
            closeBootstrap();
            log.error("Occur exception when start server:", e);
        }
    }

    private void closeBootstrap() {
        try {
            if (bootstrap != null) {
                Future<?> bossGroupShutdownFuture = bossGroup.shutdownGracefully();
                Future<?> workerGroupShutdownFuture = workerGroup.shutdownGracefully();
                bossGroupShutdownFuture.syncUninterruptibly();
                workerGroupShutdownFuture.syncUninterruptibly();
            }
        } catch (Throwable e) {
            closeBootstrap();
            log.warn("Transport failed to closed", e);
        }
    }

    protected void initServerBootstrap(NettyServerHandler nettyServerHandler) {
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                // TCP has the Nagle algorithm enabled by default,
                // which is used to send large data as fast as possible and reduce network transmission.
                .childOption(ChannelOption.TCP_NODELAY, true)
                // Enable the underlying TCP heartbeat mechanism
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                // Indicates the maximum length of the queue used by the system to temporarily store requests that have completed three-way handshakes.
                // If the connection establishment is frequent and the server is slow to create new connections, you can increase this parameter appropriately
                .option(ChannelOption.SO_BACKLOG, 128)
                // It is initialized when the client makes its first request
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // Close the connection if you do not receive a client request within 30 seconds
                        ChannelPipeline p = ch.pipeline();
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl());
                        p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS))
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("handler", nettyServerHandler);
                        //p.addLast(serviceHandlerGroup, new NettyRpcServerHandler());
                    }
                });
    }

    @Override
    public void doClose() {
        if (channel != null) {
            channel.close();
        }
        closeAllChannels();
    }

    public void close() {
        doClose();
    }

    public Collection<Channel> getChannels() {
        return Collections.emptyList();
    }

    private void closeAllChannels() {
        Collection<Channel> channelCollection = getChannels();
        if (CollectionUtils.isNotEmpty(channelCollection)) {
            for (Channel channel : channelCollection) {
                try {
                    channel.close();
                } catch (Throwable e) {
                    log.warn("Failed to close channel: {}", e.getMessage(), e);
                }
            }
        }
    }

    protected io.netty.channel.Channel getBossChannel() {
        return channel;
    }

    public InetSocketAddress getBindAddress() {
        return bindAddress;
    }

}
