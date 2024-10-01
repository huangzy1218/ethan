package com.ethan.remoting.transport.vertx.server;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingServer;
import com.ethan.remoting.transport.vertx.encrypt.AESEncryptionHelper;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;

/**
 * Vertx server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxServer implements RemotingServer {

    private Vertx vertx;
    private HttpServer server;
    private URL url;
    private SecretKey encryptionKey;

    public VertxServer(final URL url) {
        this.vertx = Vertx.vertx();
        this.url = url;
        this.server = vertx.createHttpServer();
        this.encryptionKey = AESEncryptionHelper.generateKey();
        // Handle request
        server.requestHandler(new VertxServerHandler(encryptionKey));
    }

    @Override
    public void open() {
        int port = url.getPort();
        server.listen(port, result -> {
            if (result.succeeded()) {
                log.info("server listening on port {}", port);
            } else {
                log.error("server listening failed", result.cause());
            }
        });
    }

    @Override
    public void close() {
        server.close();
        vertx.close();
    }

}
