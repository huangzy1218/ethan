package com.ethan.remoting.tansport.netty.client;

import com.ethan.common.URL;
import com.ethan.common.util.NetUtils;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.exchange.support.DefaultFuture;
import com.ethan.remoting.tansport.netty.server.NettyServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.ethan.rpc.Constants.HEARTBEAT_KEY;

/**
 * @author Huang Z.Y.
 */
class NettyClientToServerTest {

    protected NettyServer server;
    protected NettyClient client;

    protected NettyServer newServer(int port) throws RemotingException {
        URL url = URL.valueOf("exchange://127.0.0.1:" + port + "?server=netty4");
        url.addParameter(HEARTBEAT_KEY, 600 * 1000);
        return new NettyServer(url);
    }

    protected NettyClient newClient(int port) {
        URL url = URL.valueOf("exchange://127.0.0.1:" + port + "?client=netty4&timeout=300000");
        url.addParameter(HEARTBEAT_KEY, 600 * 1000);
        return new NettyClient(url);
    }

    @BeforeEach
    protected void setUp() throws Exception {
        int port = NetUtils.getAvailablePort();
        server = newServer(port);
        client = newClient(port);
    }

    @AfterEach
    protected void tearDown() {
        try {
            if (server != null) server.close();
        } finally {
            if (client != null) client.close();
        }
    }

    @Test
    public void nettyTest() throws RemotingException, ExecutionException, InterruptedException {
        Request request = new Request();
        CompletableFuture<Object> response = client.request(request);
        DefaultFuture.received(client.getChannel(), new Response(request.getId()));
        System.out.println(response.get());
    }

}