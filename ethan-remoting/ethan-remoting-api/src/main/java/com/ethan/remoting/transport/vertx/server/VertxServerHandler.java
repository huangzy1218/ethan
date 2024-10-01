package com.ethan.remoting.transport.vertx.server;

import com.alibaba.fastjson.JSON;
import com.ethan.common.RpcException;
import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.config.ServiceRepository;
import com.ethan.remoting.Codec;
import com.ethan.remoting.RpcInvocation;
import com.ethan.remoting.exchange.codec.EthanCodec;
import com.ethan.remoting.transport.vertx.encrypt.AESEncryptionHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class VertxServerHandler implements Handler<HttpServerRequest> {

    private final ServiceRepository repository = ApplicationContextHolder.getBean(ServiceRepository.class);
    private final Codec codec = new EthanCodec();

    @Setter
    private SecretKey encryptionKey;

    public VertxServerHandler(SecretKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    private static Buffer convertByteBufToVertxBuffer(ByteBuf byteBuf) {
        byte[] bytes = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(bytes);
        return Buffer.buffer(bytes);
    }

    private ByteBuf convertVertxBufferToNettyBuffer(Buffer vertxBuffer) {
        byte[] bytes = vertxBuffer.getBytes();
        return Unpooled.wrappedBuffer(bytes);
    }

    @Override
    public void handle(HttpServerRequest request) {
        request.bodyHandler(buffer -> {
            try {
                // Decrypt the incoming message
                String decryptedMessage = AESEncryptionHelper.decrypt(buffer.toString(), encryptionKey);
                log.info("Decrypted message: {}", decryptedMessage);

                // Deserialize the message to your expected object (e.g., RpcInvocation)
                RpcInvocation invocation = JSON.parseObject(decryptedMessage, RpcInvocation.class);

                // Process the invocation, using the repository or other services
                Object service = repository.getService(invocation.getServiceName());

                String responseData = JSON.toJSONString(invokeTargetMethod(invocation, service));

                // Encrypt the response
                String encryptedResponse = AESEncryptionHelper.encrypt(responseData, encryptionKey);

                // Send the response back to the client
                sendResponse(request.response(), encryptedResponse);
            } catch (Exception e) {
                log.error("Error handling message", e);
                // Optionally send an error response
                sendErrorResponse(request.response(), "Error processing request");
            }
        });
    }

    private void sendResponse(HttpServerResponse response, String responseBody) {
        response.setStatusCode(200); // Set the appropriate status code
        response.putHeader("Content-Type", "application/json"); // Set the content type
        response.end(responseBody); // Send the encrypted response back
    }

    private void sendErrorResponse(HttpServerResponse response, String errorMessage) {
        // Internal server error
        response.setStatusCode(500);
        response.putHeader("Content-Type", "text/plain");
        // Send the error message back
        response.end(errorMessage);
    }

    private Object invokeTargetMethod(com.ethan.remoting.RpcInvocation request, Object service) {
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
