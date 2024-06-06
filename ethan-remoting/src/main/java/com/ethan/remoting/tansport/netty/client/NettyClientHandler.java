package com.ethan.remoting.tansport.netty.client;

import com.ethan.common.URL;
import com.ethan.remoting.tansport.netty.RpcMessage;
import com.ethan.rpc.RpcInvocation;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Customize the client ChannelHandler to process the data sent by the server.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class NettyClientHandler extends ChannelInboundHandlerAdapter {

    private final URL url;

    public NettyClientHandler(URL url,) {
        if (url == null) {
            throw new IllegalArgumentException("url == null");
        }

        this.url = url;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof RpcInvocation) {
            log.info("Server received msg: [{}]", msg);
            RpcInvocation request = (RpcInvocation) msg;
            RpcMessage rpcMessage = new RpcMessage();
            rpcMessage.setCodec(SerializationTypeEnum.HESSIAN.getCode());
            rpcMessage.setCompress(CompressTypeEnum.GZIP.getCode());

            if (request.getMethodName().equals(RpcConstants.HEARTBEAT_REQUEST_TYPE)) {
                rpcMessage.setMessageType(RpcConstants.HEARTBEAT_RESPONSE_TYPE);
                rpcMessage.setData(RpcConstants.PONG);
            } else {
                // Execute the target method (the method the client needs to execute) and return the method result
                Object result = rpcRequestHandler.handle(rpcInvocation);
                log.info("Server got result: {}", result.toString());
                rpcMessage.setMessageType(RpcConstants.RESPONSE_TYPE);

                RpcResponse<Object> rpcResponse;
                if (ctx.channel().isActive() && ctx.channel().isWritable()) {
                    rpcResponse = RpcResponse.success(result, rpcInvocation.getServiceName());
                } else {
                    rpcResponse = RpcResponse.fail(RpcResponseCodeEnum.FAIL);
                    log.error("Channel not writable now, message dropped");
                }
                rpcMessage.setData(rpcResponse);
            }

            ctx.writeAndFlush(rpcMessage).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);

        }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleState state = ((IdleStateEvent) evt).state();
            if (state == IdleState.READER_IDLE) {
                log.info("idle check happen, so close the connection");
                ctx.close();
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.warn("Server catch exception", cause);
        ctx.close();
    }

}
