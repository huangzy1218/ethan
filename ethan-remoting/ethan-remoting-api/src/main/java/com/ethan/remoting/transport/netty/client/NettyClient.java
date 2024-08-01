package com.ethan.remoting.transport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.support.DefaultFuture;
import com.ethan.remoting.transport.AbstractEndpoint;
import com.ethan.remoting.transport.netty.NettyChannel;
import com.ethan.remoting.transport.netty.codec.NettyCodecAdapter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author Huang Z.Y.
 */
public class NettyClient extends AbstractEndpoint implements RemotingClient {

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    /**
     * Save established pipeline.
     */
    private Channel channel;

    public NettyClient(URL url) {
        super(url);
        // Initialize Netty components
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();

        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.TCP_NODELAY, true)
                .handler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) {
                        NettyCodecAdapter adapter = new NettyCodecAdapter(getCodec(), getUrl());
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("handler", new NettyClientHandler());
                    }
                });
    }

    @Override
    public void connect() {
        try {
            ChannelFuture future = bootstrap.connect(getUrl().getHost(), getUrl().getPort()).sync();
            this.channel = future.channel();
            future.channel().closeFuture().addListener(f -> group.shutdownGracefully());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Interrupted during connection", e);
        }
    }

    /**
     * Send message to server.
     *
     * @param request The content of the message to be sent
     */
    public void send(Request request) {
        if (group.isShuttingDown() || group.isShutdown()) {
            throw new IllegalStateException("The client has closed or is closing.");
        }
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("Connection not established or closed.");
        }
        com.ethan.remoting.Channel ch = NettyChannel.getOrAddChannel(channel, getUrl());
        DefaultFuture.newFuture(ch, request, getConnectTimeout());
        // Send message to server
        channel.writeAndFlush(request);
    }

}
