package com.ethan.remoting.transport.netty.test;

import com.ethan.common.URL;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.transport.netty.client.NettyClient;

/**
 * @author Huang Z.Y.
 */
public class NettyClientApplication {

    public static void main(String[] args) {
        // Create a mock URL for the client
        URL clientUrl = URL.valueOf("dubbo://127.0.0.1:8088");
        NettyClient nettyClient = new NettyClient(clientUrl);
        nettyClient.connect();
        Request request = new Request();
        request.setData("Hello World");
        nettyClient.send(request);
    }

}
