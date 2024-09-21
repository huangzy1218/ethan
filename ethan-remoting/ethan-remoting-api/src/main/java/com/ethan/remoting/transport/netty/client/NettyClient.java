package com.ethan.remoting.transport.netty.client;

import com.ethan.common.URL;
import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.transport.AbstractEndpoint;
import com.ethan.remoting.transport.netty.codec.NettyCodecAdapter;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;

import static com.ethan.common.constant.CommonConstants.DEFAULT_REGISTRY;

/**
 * @author Huang Z.Y.
 */
public class NettyClient extends AbstractEndpoint implements RemotingClient {

    private Bootstrap bootstrap;
    private EventLoopGroup group;
    private ChannelProvider channelProvider;
    private Registry registry;
    /**
     * Save established pipeline.
     */
    private Channel channel;

    public NettyClient(URL url) {
        super(url);
        // Initialize Netty components
        bootstrap = new Bootstrap();
        group = new NioEventLoopGroup();
        channelProvider = ApplicationContextHolder.getBean(ChannelProvider.class);
        registry = ApplicationModel.defaultModel().getExtensionLoader(Registry.class).getExtension(DEFAULT_REGISTRY);
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
        ChannelFuture future = bootstrap.connect(getUrl().getHost(), getUrl().getPort());
        boolean ret = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
        if (ret) {
            this.channel = future.channel();
            future.channel().closeFuture().addListener(f -> group.shutdownGracefully());
        }
    }

    /**
     * Send message to server.
     *
     * @param request The content of the message to be sent
     */
    public void send(Object request) {
        if (group.isShuttingDown() || group.isShutdown()) {
            throw new IllegalStateException("The client has closed or is closing.");
        }
        if (channel == null || !channel.isActive()) {
            throw new IllegalStateException("Connection not established or closed.");
        }
        // Send message to server
        channel.writeAndFlush(request);
//        Channel channel = getChannel(url);
//        com.ethan.remoting.Channel ch = NettyChannel.getOrAddChannel(channel, getUrl());
//        channel.writeAndFlush(request);
//        DefaultFuture future = DefaultFuture.newFuture(ch, request, getConnectTimeout());
//        try {
//            Response response = (Response) future.get(url.getParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT), TimeUnit.SECONDS);
//            return CompletableFuture.completedFuture(response);
//        } catch (InterruptedException | ExecutionException | java.util.concurrent.TimeoutException e) {
//            throw new RpcException("Cannot connect to server: " + getUrl(), e);
//        }
    }

    public Channel getChannel(URL url) {
        Channel channel = channelProvider.get(url);
        if (channel == null) {
            channel = this.channel;
            channelProvider.set(url, channel);
        }
        return channel;
    }

}
