package com.ethan.remoting.transport.netty;

import com.ethan.common.URL;
import com.ethan.remoting.transport.netty.server.NettyServer;

/**
 * @author Huang Z.Y.
 */
public class NettyServerApplication {

    public static void main(String[] args) {
        URL serverUrl = URL.valueOf("dubbo://127.0.0.1:8086");
        NettyServer nettyServer = new NettyServer(serverUrl);
        nettyServer.open();
    }

}
