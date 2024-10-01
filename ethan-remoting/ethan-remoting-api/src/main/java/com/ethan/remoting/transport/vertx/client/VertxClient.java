package com.ethan.remoting.transport.vertx.client;

import com.alibaba.fastjson2.JSON;
import com.ethan.common.URL;
import com.ethan.remoting.RemotingClient;
import com.ethan.remoting.RpcInvocation;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.transport.vertx.encrypt.AESEncryptionHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.http.HttpClientRequest;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.NetClient;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.concurrent.ExecutionException;

/**
 * Vert.x client.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxClient implements RemotingClient {

    private Vertx vertx;
    private HttpClient client;
    private URL url;
    private SecretKey encryptionKey;


    public VertxClient(URL url) {
        this.url = url;
        this.vertx = Vertx.vertx();
        this.client = vertx.createHttpClient(new HttpClientOptions().setSsl(true));
        this.encryptionKey = AESEncryptionHelper.generateKey();
    }

    public void send(Object request) throws InterruptedException, ExecutionException {
        // Send TCP request
        NetClient netClient = vertx.createNetClient();

        if (request instanceof Request) {
            RpcInvocation invocation = (RpcInvocation) ((Request) request).getData();
            String jsonPayload = JSON.toJSONString(invocation);

            // Encrypt the payload
            String encryptedPayload = AESEncryptionHelper.encrypt(jsonPayload, encryptionKey);
            client.request(HttpMethod.POST, url.toString(),
                    result -> {
                        if (!result.succeeded()) {
                            log.error("Connect failed", result.cause());
                            return;
                        }
                        HttpClientRequest res = result.result();
                        res.write(JSON.toJSONString(encryptedPayload));
                    });
        }
    }

    @Override
    public void connect() throws Throwable {
        client.close();
        vertx.close();
    }

    public SecretKey getEncryptionKey() {
        return encryptionKey;
    }

}
