package com.ethan.remoting.transport.vertx.server;

import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.config.ServiceRepository;
import com.ethan.remoting.Codec;
import com.ethan.remoting.RpcInvocation;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.exchange.codec.EthanCodec;
import com.ethan.remoting.transport.CodecSupport;
import com.ethan.remoting.transport.vertx.TcpBufferHandlerWrapper;
import com.ethan.rpc.AppResponse;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.net.NetSocket;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxServerHandler implements Handler<NetSocket> {

    private final ServiceRepository repository = ApplicationContextHolder.getBean(ServiceRepository.class);
    private final Codec codec = new EthanCodec();

    private static Buffer convertByteBufToVertxBuffer(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return Buffer.buffer(bytes);
    }

    @Override
    public void handle(NetSocket request) {
        Serialization serialization = CodecSupport.getSerialization((byte) 1);
        TcpBufferHandlerWrapper bufferHandlerWrapper = new TcpBufferHandlerWrapper(buffer -> {
            Object req = null;
            try {
                req = codec.decode(null, convertVertxBufferToNettyBuffer(buffer));
            } catch (Exception e) {
                log.error("Decode error", e);
            }
            if (req instanceof Request newRequest) {
                RpcInvocation invocation = (RpcInvocation) (newRequest.getData());
                Response response = new Response(newRequest.getId());
                AppResponse appResponse = new AppResponse();
                try {
                    Class<?> implClass = repository.getService(invocation.getServiceName()).getClass();
                    Method method = implClass.getMethod(invocation.getMethodName(), invocation.getParameterTypes());
                    Object result = method.invoke(implClass.newInstance(), invocation.getArguments());
                    // Encapsulate return results
                    appResponse.setValue(result);
                    response.setResult(appResponse);
                } catch (Exception e) {
                    log.info("Invoke method {} failed", invocation.getMethodName(), e);
                    throw new RuntimeException(e);
                }
            }
        });
        request.handler(bufferHandlerWrapper);
    }

    void doResponse(HttpServerRequest request, Response rpcResponse, Serialization serializer) {
        HttpServerResponse httpServerResponse = request.response()
                .putHeader("content-type", "application/json");
        try {
            // Serialization
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutput objectOutput = serializer.serialize(baos);
            objectOutput.writeObject(rpcResponse);
            baos.flush();
            byte[] serialized = baos.toByteArray();
            httpServerResponse.end(Buffer.buffer(serialized));
        } catch (IOException e) {
            httpServerResponse.end(Buffer.buffer());
            log.error("Cannot serialize RpcInvocation", e);
        }
    }

    private ByteBuf convertVertxBufferToNettyBuffer(Buffer vertxBuffer) {
        byte[] bytes = vertxBuffer.getBytes();
        return Unpooled.wrappedBuffer(bytes);
    }

}
