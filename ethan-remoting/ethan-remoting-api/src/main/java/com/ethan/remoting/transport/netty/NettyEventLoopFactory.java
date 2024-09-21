package com.ethan.remoting.transport.netty;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.concurrent.ThreadFactory;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * Factory class for creating Netty EventLoopGroups.
 *
 * @author Huang Z,Y.
 */
public class NettyEventLoopFactory {

    /**
     * Create an EventLoopGroup with the specified number of threads and thread factory name.
     *
     * @param threads           Thread number
     * @param threadFactoryName ThreadFactory name
     * @return An EventLoopGroup instance
     */
    public static EventLoopGroup eventLoopGroup(int threads, String threadFactoryName) {
        ThreadFactory threadFactory = new DefaultThreadFactory(threadFactoryName, true);
        return shouldEpoll()
                ? new EpollEventLoopGroup(threads, threadFactory)
                : new NioEventLoopGroup(threads, threadFactory);
    }

    /**
     * Checks whether Epoll should be used.
     *
     * @return {@code true} if Epoll should be used
     */
    public static boolean shouldEpoll() {
        if (Boolean.parseBoolean(System.getProperty(NETTY_EPOLL_ENABLE_KEY, "false"))) {
            String osName = System.getProperty(OS_NAME_KEY);
            return osName.toLowerCase().contains(OS_LINUX_PREFIX) && Epoll.isAvailable();
        }

        return false;
    }

    public static Class<? extends ServerSocketChannel> serverSocketChannelClass() {
        return shouldEpoll() ? EpollServerSocketChannel.class : NioServerSocketChannel.class;
    }

}
