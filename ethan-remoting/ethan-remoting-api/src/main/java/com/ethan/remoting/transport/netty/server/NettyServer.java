package com.ethan.remoting.transport.netty.server;

import com.ethan.common.URL;
import com.ethan.common.util.UrlUtils;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.transport.AbstractEndpoint;
import com.ethan.remoting.transport.netty.NettyEventLoopFactory;
import com.ethan.remoting.transport.netty.codec.NettyCodecAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

import static com.ethan.common.constant.CommonConstants.DEFAULT_IO_THREADS;
import static com.ethan.common.constant.CommonConstants.*;
import static com.ethan.remoting.RemotingConstants.*;
import static java.util.concurrent.TimeUnit.MILLISECONDS;


/**
 * Netty server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServer extends AbstractEndpoint implements RemotingServer {

    /**
     * The cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    private Map<String, Channel> channels;
    /**
     * Netty server bootstrap.
     */
    private ServerBootstrap bootstrap;
    /**
     * The boss channel that receive connections and dispatch these to worker channel.
     */
    private io.netty.channel.Channel channel;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(URL url) throws RemotingException {
        super(url);
    }

    /**
     * Init and start netty server
     *
     * @throws Throwable
     */
    protected void doOpen() {
        bootstrap = new ServerBootstrap();

        bossGroup = createBossGroup();
        workerGroup = createWorkerGroup();

        final NettyServerHandler nettyServerHandler = createNettyServerHandler();
        channels = nettyServerHandler.getChannels();

        initServerBootstrap(nettyServerHandler);

        // bind
        try {
            ChannelFuture channelFuture = bootstrap.bind((getUrl().getPort()));
            channelFuture.syncUninterruptibly();
            channel = channelFuture.channel();
        } catch (Throwable t) {
            closeBootstrap();
            throw t;
        }
    }

    @Override
    public void open() {
        doOpen();
    }

    protected EventLoopGroup createBossGroup() {
        return NettyEventLoopFactory.eventLoopGroup(1, EVENT_LOOP_BOSS_POOL_NAME);
    }

    protected EventLoopGroup createWorkerGroup() {
        return NettyEventLoopFactory.eventLoopGroup(
                getUrl().getPositiveParameter(IO_THREADS_KEY, DEFAULT_IO_THREADS),
                EVENT_LOOP_WORKER_POOL_NAME);
    }

    protected NettyServerHandler createNettyServerHandler() {
        return new NettyServerHandler();
    }

    protected void initServerBootstrap(NettyServerHandler nettyServerHandler) {
        boolean keepalive = getUrl().getParameter(KEEP_ALIVE_KEY, Boolean.FALSE);
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NettyEventLoopFactory.serverSocketChannelClass())
                .option(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_KEEPALIVE, keepalive)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        int closeTimeout = UrlUtils.getCloseTimeout(getUrl());
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl());
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("server-idle-handler", new IdleStateHandler(0, 0, closeTimeout, MILLISECONDS))
                                .addLast("handler", nettyServerHandler);
                    }
                });
    }

    @Override
    public void close() {
        doClose();
    }

    protected void doClose() {
        try {
            if (channel != null) {
                // unbind.
                channel.close();
            }
        } catch (Throwable e) {
            log.warn("Transport failed to closed: {}", e.getMessage(), e);
        }
        closeBootstrap();
        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            log.warn("Transport failed to closed: {}", e.getMessage(), e);
        }
    }

    private void closeBootstrap() {
        try {
            if (bootstrap != null) {
                long timeout = getUrl().getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT);
                long quietPeriod = Math.min(2000L, timeout);
                Future<?> bossGroupShutdownFuture = bossGroup.shutdownGracefully(quietPeriod, timeout, MILLISECONDS);
                Future<?> workerGroupShutdownFuture =
                        workerGroup.shutdownGracefully(quietPeriod, timeout, MILLISECONDS);
                bossGroupShutdownFuture.syncUninterruptibly();
                workerGroupShutdownFuture.syncUninterruptibly();
            }
        } catch (Throwable e) {
            log.warn("Transport failed to closed: {}", e.getMessage(), e);
        }
    }

    public int getChannelsSize() {
        return channels.size();
    }

}
