package com.ethan.remoting.transport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.transport.netty.server.NettyServer;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;

public class NettyHandlerTest {

    private NettyServer nettyServer;
    private NettyClient nettyClient;
    private EmbeddedChannel embeddedChannel;

    @BeforeEach
    public void setUp() throws RemotingException {
        // Create a mock URL for the server
        URL serverUrl = URL.valueOf("dubbo://localhost:8080");
        nettyServer = new NettyServer(serverUrl);

        // Create a mock URL for the client
        URL clientUrl = URL.valueOf("dubbo://localhost:8080");
        nettyClient = new NettyClient(clientUrl);

        // Create an EmbeddedChannel to simulate the server's channel
        embeddedChannel = new EmbeddedChannel(new ChannelInboundHandlerAdapter() {
            @Override
            public void channelRead(ChannelHandlerContext ctx, Object msg) {
                // Simulate processing the received message
                ctx.writeAndFlush(msg); // Echo the message back
            }
        });
    }

    @Test
    public void testClientServerCommunication() throws RemotingException {
        String message = "Hello, Netty!";

        // Connect the client to the server
        nettyClient.doConnect();

        // Send a message from client to server
        CompletableFuture<Object> future = nettyClient.request(message);

        future.thenAccept(response -> {
            assertEquals(message, response);
            System.out.println(response);
        });
//
//        // Retrieve the sent message from the EmbeddedChannel
//        String sentMessage = (String) embeddedChannel.readOutbound();
//
//        // Assert that the sent message matches
//        assertEquals(message, sentMessage);
    }

    @AfterEach
    public void tearDown() {
        if (nettyClient != null) {
            nettyClient.close();
        }
        if (nettyServer != null) {
            nettyServer.close();
        }
        if (embeddedChannel != null) {
            embeddedChannel.close();
        }
    }

}