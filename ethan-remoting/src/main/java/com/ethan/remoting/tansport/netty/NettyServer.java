package com.ethan.remoting.tansport.netty;

import com.ethan.common.URL;
import com.ethan.common.util.NamedThreadFactory;
import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.RemotingServer;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.ethan.remoting.RemotingConstants.*;

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
        ChannelFactory channelFactory = new NioServerSocketChannelFactory(
                boss, worker, getUrl().getPositiveParameter(IO_THREADS_KEY, DEFAULT_IO_THREADS));
    }

    @Override
    public void doClose() {

    }

    @Override
    public Collection<Channel> getChannels() {
        return List.of();
    }

}
