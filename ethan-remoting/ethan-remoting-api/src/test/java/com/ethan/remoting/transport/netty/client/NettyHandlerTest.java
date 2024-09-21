package com.ethan.remoting.transport.netty.client;

import com.ethan.common.RemotingException;
import com.ethan.common.URL;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.transport.netty.server.NettyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutionException;

public class NettyHandlerTest {

    private NettyServer nettyServer;
    private NettyClient nettyClient;

    @BeforeEach
    public void setUp() throws Throwable {
        // Create a mock URL for the server
        URL serverUrl = URL.valueOf("dubbo://127.0.0.1:8084");
        nettyServer = new NettyServer(serverUrl);
        nettyServer.open();
        // Create a mock URL for the client
        URL clientUrl = URL.valueOf("dubbo://127.0.0.1:8084");
        nettyClient = new NettyClient(clientUrl);
        nettyClient.connect();
    }

    @Test
    void testClientServerCommunication() throws RemotingException, ExecutionException, InterruptedException {
        String message = "Hello, Netty";

        // Send a message from client to server
        Request request = new Request();
        request.setData("Hello World");
        nettyClient.send(request);
    }

    @Test
    public void testNewFuture() {
//        Request request = new Request();
//        DefaultFuture future = DefaultFuture.newFuture(nettyClient.getChannel(), request, 1000);
//        future.sent(request);
//        Response response = new Response(request.getId());
//        future.received(response);
    }

    @AfterEach
    public void tearDown() throws Throwable {
//        if (nettyClient != null) {
//            nettyClient.close();
//        }
//        if (nettyServer != null) {
//            nettyServer.close();
//        }
    }

}