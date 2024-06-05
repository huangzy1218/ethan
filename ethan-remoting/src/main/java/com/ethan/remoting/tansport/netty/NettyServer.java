package com.ethan.remoting.tansport.netty;

import com.ethan.common.URL;
import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.NamedThreadFactory;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.RemotingServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
     * <ip:port, channel>
     */
    private Map<String, Channel> channels;

    private ServerBootstrap bootstrap;

    private org.jboss.netty.channel.Channel channel;

    public NettyServer(URL url) throws RemotingException {
        this.url = url;
    }

    @Override
    public void doOpen() {
        ExecutorService boss = Executors.newCachedThreadPool(new NamedThreadFactory(EVENT_LOOP_BOSS_POOL_NAME,
                true));
        ExecutorService worker =
                Executors.newCachedThreadPool(new NamedThreadFactory(EVENT_LOOP_WORKER_POOL_NAME, true));
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(boss, worker);
        bootstrap = new ServerBootstrap(channelFactory);
        bootstrap.setOption("child.tcpNoDelay", true);
        channel = bootstrap.bind();
    }

    @Override
    public void doClose() {
        try {
            if (channel != null) {
                // Unbind
                channel.close();
            }
        } catch (Throwable e) {
            log.warn("Transport closed failed: {}", e.getMessage(), e);
        }
        try {
            Collection<Channel> channels = getChannels();
            if (CollectionUtils.isNotEmpty(channels)) {
                for (Channel channel : channels) {
                    try {
                        channel.close();
                    } catch (Throwable e) {
                        log.warn("Transport closed failed: {}", e.getMessage(), e);
                    }
                }
            }
        } catch (Throwable e) {
            log.warn("Transport closed failed: {}", e.getMessage(), e);
        }
        try {
            if (bootstrap != null) {
                // Release external resource.
                bootstrap.releaseExternalResources();
            }
        } catch (Throwable e) {
            log.warn("Transport closed failed: {}", e.getMessage(), e);
        }
        try {
            if (channels != null) {
                channels.clear();
            }
        } catch (Throwable e) {
            log.warn("Transport closed failed: {}", e.getMessage(), e);
        }
    }

    @Override
    public Collection<Channel> getChannels() {
        Collection<Channel> chs = new ArrayList<>(this.channels.size());
        // Pick channels from NettyServerHandler (needless to check connectivity)
        chs.addAll(this.channels.values());
        return chs;
    }

}
