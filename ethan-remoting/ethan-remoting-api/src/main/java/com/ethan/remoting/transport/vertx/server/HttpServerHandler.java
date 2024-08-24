package com.ethan.remoting.transport.vertx.server;

import com.ethan.common.RpcException;
import com.ethan.common.context.ApplicationContextHolder;
import com.ethan.config.ServiceRepository;
import com.ethan.remoting.RpcInvocation;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.remoting.transport.CodecSupport;
import com.ethan.rpc.AppResponse;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class HttpServerHandler implements Handler<HttpServerRequest> {

    private final ServiceRepository repository = ApplicationContextHolder.getBean(ServiceRepository.class);

    @Override
    public void handle(HttpServerRequest request) {
        Serialization serialization = CodecSupport.getSerialization((byte) 1);

        request.bodyHandler(body -> {
            byte[] bytes = body.getBytes();
            Request req = null;
            RpcInvocation rpcInvocation = null;
            try {
                InputStream inputStream = new ByteArrayInputStream(bytes);
                ObjectInput objectInput = serialization.deserialize(inputStream);
                req = objectInput.readObject(Request.class);
            } catch (IOException | ClassNotFoundException e) {
                log.error("Cannot deserialize RpcInvocation", e);
                throw new RpcException(e);
            }
            // Build response
            Response response = new Response();
            response.setId(req.getId());
            // If request is empty, return directly
            if (rpcInvocation == null) {
                response.setErrorMsg("RpcInvocation is null");
                return;
            }
            AppResponse appResponse = new AppResponse();
            try {
                Class<?> implClass = repository.getService(rpcInvocation.getServiceName()).getClass();
                Method method = implClass.getMethod(rpcInvocation.getMethodName(), rpcInvocation.getParameterTypes());
                Object result = method.invoke(implClass.newInstance(), rpcInvocation.getArguments());
                // Encapsulate return results
                appResponse.setValue(result);
                response.setResult(appResponse);
            } catch (Exception e) {
                log.info("Invoke method {} failed", rpcInvocation.getMethodName(), e);
                ;
                throw new RuntimeException(e);
            }
        });

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

}
