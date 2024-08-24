package com.ethan.remoting.transport.vertx.client;

import com.ethan.common.URL;
import com.ethan.remoting.Codec;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.RpcInvocation;
import com.ethan.remoting.exchange.Request;
import com.ethan.rpc.AppResponse;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.net.NetClient;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxClient implements RemotingClient {

    private Vertx vertx;
    private HttpClient client;
    private URL url;
    private Codec codec;

    public VertxClient(URL url) {
        this.url = url;
    }

    public void send(Object request) throws InterruptedException, ExecutionException {
        // Send TCP request
        Vertx vertx = Vertx.vertx();
        NetClient netClient = vertx.createNetClient();
        CompletableFuture<AppResponse> responseFuture = new CompletableFuture<>();
        if (request instanceof Request) {
            RpcInvocation invocation = (RpcInvocation) ((Request) request).getData();
            netClient.connect(url.getPort(), url.getHost(),
                    result -> {
                        if (!result.succeeded()) {
                            log.error("connect failed", result.cause());
                            return;
                        }
                        NetSocket socket = result.result();
                        socket.write(invocation.toString());
                    });
        }
    }


    @Override
    public void connect() throws Throwable {
        client.close();
    }
    
}
