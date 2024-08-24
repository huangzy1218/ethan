package com.ethan.remoting.transport.vertx.server;

import com.ethan.common.URL;
import com.ethan.remoting.RemotingServer;
import io.vertx.core.Vertx;
import io.vertx.core.net.NetServer;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxServer implements RemotingServer {

    private Vertx vertx;
    private NetServer server;
    private URL url;

    public VertxServer(final URL url) {
        vertx = Vertx.vertx();
        this.url = url;
        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        // Handle request
        server.requestHandler(new HttpServerHandler());
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
    }

}
