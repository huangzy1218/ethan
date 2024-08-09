package com.ethan.remoting.transport.netty.server;

import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.config.ServiceRepository;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.RpcInvocation;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Netty server handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
@ChannelHandler.Sharable
public class NettyServerHandler extends ChannelDuplexHandler {

    private final ServiceRepository serviceRepository;

    public NettyServerHandler() {
        serviceRepository = ApplicationContextHolder.getBean(ServiceRepository.class);
    }

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
            Response response = new Response(request.getId(), request.getVersion());
            if (request.isBroken()) {
                Object data = request.getData();
                String msg;
                if (data == null) {
                    msg = null;
                } else {
                    msg = data.toString();
                }
                response.setErrorMsg("Fail to decode request due to: " + msg);
                response.setStatus(Response.BAD_REQUEST);
            }
            RpcInvocation invocation = (RpcInvocation) request.getData();
            Object service = serviceRepository.getService(invocation.getServiceName());
            Object res = invokeTargetMethod(invocation, service);
            if (Objects.nonNull(res)) {
                response.setStatus(Response.OK);
                response.setResult(res);
            } else {
                response.setStatus(Response.SERVICE_ERROR);
                response.setErrorMsg(res.toString());
            }
            ctx.writeAndFlush(response);
        } catch (Exception e) {
            log.error("Error processing request: {}", request, e);
            Response errorResponse = createErrorResponse(request, e);
            ctx.writeAndFlush(errorResponse);
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

    private Object invokeTargetMethod(RpcInvocation request, Object service) {
        Object result;
        try {
            Method method = service.getClass().getMethod(request.getMethodName(), request.getParameterTypes());
            result = method.invoke(service, request.getParameters());
            log.info("Service: [{}] successful invoke method [{}]", request.getServiceName(), request.getMethodName());
        } catch (NoSuchMethodException | IllegalArgumentException | InvocationTargetException |
                 IllegalAccessException e) {
            throw new RpcException(e.getMessage(), e);
        }
        return result;
    }

}
