package com.ethan.remoting.transport.netty.server;

import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletionStage;

/**
 * Netty server handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("Netty server channel active: {}", ctx.channel().remoteAddress());
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof Request) {
            Request request = (Request) msg;
            log.info("Received request: {}", request);
            handleRequest(request, ctx);
            ctx.fireChannelRead(msg);
        } else {
            log.warn("Received unexpected message type: {}", msg.getClass());
            ctx.fireChannelRead(msg);
        }
        ctx.flush();
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

    private void processRequest(Channel channel, Request req) throws RemotingException {
        Response res = new Response(req.getId(), req.getVersion());
        if (req.isBroken()) {
            Object data = req.getData();
            String msg;
            if (data == null) {
                msg = null;
            } else {
                msg = data.toString();
            }
            res.setErrorMsg("Fail to decode request due to: " + msg);
            res.setStatus(Response.BAD_REQUEST);
            channel.send(res);
            return;
        }
        // find handler by message class.
        Object msg = req.getData();
        try {
            CompletionStage<Object> future = handler.reply(channel, msg);
            future.whenComplete((appResult, t) -> {
                try {
                    if (t == null) {
                        res.setStatus(Response.OK);
                        res.setResult(appResult);
                    } else {
                        res.setStatus(Response.SERVICE_ERROR);
                        res.setErrorMsg(t.toString());
                    }
                    channel.send(res);
                } catch (RemotingException e) {
                    log.warn("Send result to consumer failed, channel is {}, msg is {}", channel, e);
                }
            });
        } catch (Throwable e) {
            res.setStatus(Response.SERVICE_ERROR);
            res.setErrorMsg(e.toString());
            channel.send(res);
        }
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
