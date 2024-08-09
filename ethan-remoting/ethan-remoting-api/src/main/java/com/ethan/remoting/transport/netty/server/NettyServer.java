package com.ethan.remoting.transport.netty.server;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.transport.AbstractEndpoint;
import com.ethan.remoting.transport.netty.codec.NettyCodecAdapter;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServer extends AbstractEndpoint implements RemotingServer {

    private ServerBootstrap bootstrap;
    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(final URL url) {
        super(url);
        // Initialize Netty components
        bootstrap = new ServerBootstrap();
        // 1 boss thread
        bossGroup = new NioEventLoopGroup(1);
        // Default number of worker threads
        workerGroup = new NioEventLoopGroup();

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl());
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("handler", new NettyServerHandler());
                    }
                });
    }

    /**
     * Start the Netty server and bind the specified port.
     */
    @Override
    public void open() {
        try {
            bootstrap.bind(getUrl().getPort()).sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Waiting for Netty server startup to be interrupted", e);
        }
    }

    /**
     * Shut down the Netty server and release resources.
     */
    @Override
    public void close() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Netty server is down.");
        } catch (Exception e) {
            log.error("Exception when shutting down Netty server", e);
        }
    }

}
