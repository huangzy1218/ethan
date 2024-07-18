package com.ethan.remoting.transport.netty.server;

import com.ethan.remoting.Channel;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.exchange.support.DefaultFuture;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Netty server handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    /**
     * The cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    @Getter
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Netty server channel active.");
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Request) {
            Request request = (Request) msg;
            log.info("Received request: {}", request);
            handleRequest(request, ctx);
        } else {
            log.warn("Received unexpected message type: {}", msg.getClass());
        }
    }

    private void handleRequest(Request request, ChannelHandlerContext ctx) {
        try {
            // Process the request and generate a response
            Response response = processRequest(request);
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            log.error("Error processing request: {}", request, e);
            Response errorResponse = createErrorResponse(request, e);
            ctx.writeAndFlush(errorResponse);
        }
    }

    private Response processRequest(Request request) {
        Response response = new Response();
        response.setId(request.getId());
        DefaultFuture.received(response);
        return response;
    }

    private Response createErrorResponse(Request request, Exception e) {
        Response response = new Response();
        response.setId(request.getId());
        response.setErrorMsg("Error processing request: " + e.getMessage());
        return response;
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught in NettyServerHandler", cause);
        ctx.close();
    }

}
