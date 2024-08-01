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
        // 初始化 Netty 组件
        bootstrap = new ServerBootstrap();
        // 1 个 boss 线程
        bossGroup = new NioEventLoopGroup(1);
        // 默认的 worker 线程数
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
     * 启动 Netty 服务器并绑定指定端口
     */
    @Override
    public void open() {
        try {
            bootstrap.bind(getUrl().getPort()).sync();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("等待 Netty 服务器启动过程中被中断", e);
        }
    }

    /**
     * 关闭 Netty 服务器，释放资源
     */
    @Override
    public void close() {
        try {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
            log.info("Netty 服务器已关闭");
        } catch (Exception e) {
            log.error("关闭 Netty 服务器时出现异常", e);
        }
    }

}
