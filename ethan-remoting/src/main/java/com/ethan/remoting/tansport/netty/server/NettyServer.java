package com.ethan.remoting.tansport.netty.server;

import com.ethan.common.URL;
import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.NamedThreadFactory;
import com.ethan.common.util.RuntimeUtils;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.tansport.netty.NettyEventLoopFactory;
import com.ethan.rpc.Constants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.DefaultEventExecutorGroup;
import io.netty.util.concurrent.Future;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.ethan.remoting.RemotingConstants.EVENT_LOOP_BOSS_POOL_NAME;
import static com.ethan.remoting.RemotingConstants.EVENT_LOOP_WORKER_POOL_NAME;

/**
 * Netty server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServer implements RemotingServer {

    @Getter
    private volatile URL url;

    /**
     * <ip:port, channel>.
     */
    private Map<String, Channel> channels;

    private ServerBootstrap bootstrap;

    private io.netty.channel.Channel channel;

    EventLoopGroup bossGroup;
    EventLoopGroup workerGroup;

    public NettyServer(URL url) throws RemotingException {
        this.url = url;
    }

    @Override
    public void doOpen() {
        bootstrap = new ServerBootstrap();

        String bindIp = getUrl().getParameter(Constants.BIND_IP_KEY, getUrl().getHost());
        int bindPort = Integer.parseInt(getUrl().getParameter(Constants.BIND_PORT_KEY, String.valueOf(getUrl().getPort())));
        final NettyServerHandler nettyServerHandler = new NettyServerHandler(getUrl());
        channels = nettyServerHandler.getChannels();

        bossGroup = NettyEventLoopFactory.eventLoopGroup(1, EVENT_LOOP_BOSS_POOL_NAME);
        workerGroup = NettyEventLoopFactory.eventLoopGroup(Constants.DEFAULT_IO_THREADS, EVENT_LOOP_WORKER_POOL_NAME);

        DefaultEventExecutorGroup serviceHandlerGroup = new DefaultEventExecutorGroup(
                RuntimeUtils.cpus() * 2,
                new NamedThreadFactory("service-handler-group", false)
        );
        initServerBootstrap();
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

    protected void initServerBootstrap() {
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
                .handler(new LoggingHandler(LogLevel.INFO))
                // It is initialized when the client makes its first request
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        // Close the connection if you do not receive a client request within 30 seconds
                        ChannelPipeline p = ch.pipeline();
                        p.addLast(new IdleStateHandler(30, 0, 0, TimeUnit.SECONDS));
                        //p.addLast(new RpcMessageEncoder());
                        //p.addLast(new RpcMessageDecoder());
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
        channels.clear();
    }

    @Override
    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new ArrayList<>(this.channels.size());
        // Pick channels from NettyServerHandler (needless to check connectivity)
        chs.addAll(this.channels.values());
        return chs;
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

    protected Map<String, Channel> getServerChannels() {
        return channels;
    }

}
