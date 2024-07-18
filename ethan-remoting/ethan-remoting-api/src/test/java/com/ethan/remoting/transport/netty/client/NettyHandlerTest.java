package com.ethan.remoting.transport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.exchange.support.DefaultFuture;
import com.ethan.remoting.transport.netty.server.NettyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

public class NettyHandlerTest {

    private NettyServer nettyServer;
    private NettyClient nettyClient;

    @BeforeEach
    public void setUp() throws RemotingException {
        // Create a mock URL for the server
        URL serverUrl = URL.valueOf("dubbo://localhost:8080");
        nettyServer = new NettyServer(serverUrl);

        // Create a mock URL for the client
        URL clientUrl = URL.valueOf("dubbo://localhost:8080");
        nettyClient = new NettyClient(clientUrl);
    }

    @Test
    public void testClientServerCommunication() throws RemotingException {
        String message = "Hello, Netty!";

        // Send a message from client to server
        CompletableFuture<Object> future = nettyClient.request(message);
        future.thenAccept(response -> {
            System.out.println("Received response: " + response);
        }).exceptionally(e -> {
            return null;
        });
    }

    @Test
    public void testNewFuture() {
        Request request = new Request();
        DefaultFuture future = DefaultFuture.newFuture(nettyClient.getChannel(), request, 1000);
        future.sent(request);
        Response response = new Response(request.getId());
        future.received(response);
    }

    @AfterEach
    public void tearDown() {
        if (nettyClient != null) {
            nettyClient.close();
        }
        if (nettyServer != null) {
            nettyServer.close();
        }
    }

}