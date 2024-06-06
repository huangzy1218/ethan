package com.ethan.remoting.tansport.netty.server;

import com.ethan.common.URL;
import com.ethan.remoting.Channel;
import io.netty.channel.ChannelDuplexHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty server handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServerHandler extends ChannelDuplexHandler {

    /**
     * The cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    @Getter
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final URL url;

    public NettyServerHandler(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        this.url = url;
    }

}
