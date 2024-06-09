package com.ethan.remoting.tansport.netty.client;

import com.ethan.common.URL;
import com.ethan.common.context.BeanProvider;
import com.ethan.rpc.AppResponse;
import com.ethan.rpc.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import static com.ethan.rpc.Constants.HEARTBEAT_RESPONSE_TYPE;
import static com.ethan.rpc.Constants.RESPONSE_TYPE;


/**
 * Customize the client ChannelHandler to process the data sent by the server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final URL url;
    private final UnprocessedRequests unprocessedRequests;


    public NettyClientHandler(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        this.unprocessedRequests = BeanProvider.getBean(UnprocessedRequests.class);
        this.url = url;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        try {
            if (msg instanceof Message) {
                Message mess = (Message) msg;
                byte messageType = mess.getMessageType();
                if (messageType == HEARTBEAT_RESPONSE_TYPE) {
                    log.info("heart [{}]", mess.getData());
                } else if (messageType == RESPONSE_TYPE) {
                    AppResponse rpcResponse = (AppResponse) mess.getData();
                    unprocessedRequests.complete(rpcResponse);
                }
            }
        } finally {
            // Release the received message to avoid memory leaks
            ctx.fireChannelRead(msg);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error("Exception caught in NettyClientHandler", cause);
        ctx.close();
    }

}
