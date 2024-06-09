package com.ethan.remoting.tansport.netty.server;

import com.ethan.common.URL;
import com.ethan.common.enumeration.CompressType;
import com.ethan.common.enumeration.SerializationType;
import com.ethan.remoting.Channel;
import com.ethan.remoting.exchange.Request;
import com.ethan.rpc.Message;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.ReferenceCountUtil;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.rpc.Constants.*;

/**
 * Netty server handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyServerHandler extends ChannelInboundHandlerAdapter {

    /**
     * The cache for alive worker channel.
     * <ip:port, dubbo channel>
     */
    @Getter
    private final Map<String, Channel> channels = new ConcurrentHashMap<>();

    private final URL url;

    public NettyServerHandler(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }
        this.url = url;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        log.info("Invoke the channel read method");
        try {
            if (msg instanceof Message) {
                log.info("Server receive msg: [{}] ", msg);
                byte messageType = ((Message) msg).getMessageType();
                Message message = new Message();
                message.setCodec(SerializationType.HESSIAN2.getCode());
                message.setCompress(CompressType.GZIP.getCode());
                if (messageType == HEARTBEAT_REQUEST_TYPE) {
                    message.setMessageType(HEARTBEAT_RESPONSE_TYPE);
                    message.setData(PONG);
                } else {
                    Request rpcRequest = (Request) ((Request) msg).getData();
                    // Execute the target method (the method the client needs to execute) and return the method result
                    //Object result = requestHandler.handle(rpcRequest);
                    //log.info(String.format("server get result: %s", result.toString()));
                    message.setMessageType(RESPONSE_TYPE);
                    //if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    //    Response rpcResponse = RpcResponse.success(result, rpcRequest.getRequestId());
                    //    rpcMessage.setData(rpcResponse);
                    //} else {
                    //    Response<Object> rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    //    rpcMessage.setData(rpcResponse);
                    //    log.error("not writable now, message dropped");
                    //}
                }
                ctx.writeAndFlush(message).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
            }
        } finally {
            //Ensure that ByteBuf is released, otherwise there may be memory leaks
            ReferenceCountUtil.release(msg);
        }
    }

}
