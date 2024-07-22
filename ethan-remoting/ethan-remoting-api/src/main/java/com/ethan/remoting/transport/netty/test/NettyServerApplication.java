package com.ethan.remoting.transport.netty.test;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.transport.netty.server.NettyServer;

/**
 * @author Huang Z.Y.
 */
public class NettyServerApplication {

    public static void main(String[] args) throws RemotingException, InterruptedException {
        URL serverUrl = URL.valueOf("dubbo://127.0.0.1:8088");
        NettyServer nettyServer = new NettyServer(serverUrl);
        nettyServer.start();
    }

}
