package com.ethan.remoting.transport.netty.client;

import com.ethan.remoting.exchange.Response;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;


/**
 * Customize the client ChannelHandler to process the data sent by the server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyClientHandler extends ChannelDuplexHandler {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Netty client channel active: {}", ctx.channel().remoteAddress());
        ctx.flush();
    }

    /**
     * Read the information returned from the server.
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Response) {
            Response response = (Response) msg;
            // Handle the response
            handleResponse(response);
            ctx.fireChannelRead(msg);
        } else {
            log.warn("Unexpected message type: {}", msg.getClass().getName());
            // Pass it along the pipeline if not handled
            ctx.fireChannelRead(msg);
        }
        ctx.flush();
    }

    private void handleResponse(Response response) {
//        unprocessedRequests.completeRequest(response.getId(), response);
        log.info("Response processed successfully for requestId: {}", response.getId());
    }


    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
//        log.error("Exception caught in NettyClientHandler", cause);
        try {
            throw cause;
        } catch (Throwable e) {
            throw new RuntimeException(e);
        } finally {
            ctx.close();
        }
    }

}
