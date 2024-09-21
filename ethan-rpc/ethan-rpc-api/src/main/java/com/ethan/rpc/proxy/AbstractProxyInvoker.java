package com.ethan.rpc.proxy;

import com.ethan.common.URL;
import com.ethan.common.constant.CommonConstants;
import com.ethan.rpc.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * This Invoker works on provider side, delegates RPC to interface implementation.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public abstract class AbstractProxyInvoker<T> implements Invoker<T> {

    private final T proxy;
    private final Class<T> type;
    private URL url;

    protected AbstractProxyInvoker(T proxy, Class<T> type, URL url) {
        this.proxy = proxy;
        this.type = type;
        this.url = url;
    }

    protected AbstractProxyInvoker(T proxy, Class<T> type) {
        this(proxy, type, URL.buildFixedURL());
    }

    @Override
    public Result invoke(Invocation invocation) throws RpcException {
        try {
            Object value = doInvoke(proxy, invocation.getMethodName(),
                    invocation.getParameterTypes(), invocation.getParameters());
            CompletableFuture<Object> future = wrapWithFuture(value, invocation);
            CompletableFuture<AppResponse> appResponseFuture = future.handle((obj, t) -> {
                AppResponse result = new AppResponse(invocation);
                if (t != null) {
                    if (t instanceof CompletionException) {
                        result.setException(t.getCause());
                    } else {
                        result.setException(t);
                    }
                } else {
                    result.setValue(obj);
                }
                return result;
            });
            return new AsyncRpcResult(appResponseFuture, invocation);
        } catch (Throwable e) {
            throw new RpcException(
                    "Failed to invoke remote proxy method " + invocation.getMethodName() + ", cause: " + e.getMessage(),
                    e);
        }
    }

    protected abstract Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
            throws Throwable;

    @SuppressWarnings("unchecked")
    private CompletableFuture<Object> wrapWithFuture(Object value, Invocation invocation) {
        if (value instanceof CompletableFuture) {
            invocation.put(CommonConstants.PROVIDER_ASYNC_KEY, Boolean.TRUE);
            return (CompletableFuture<Object>) value;
        }
        return CompletableFuture.completedFuture(value);
    }


    @Override
    public Class<T> getInterface() {
        return type;
    }

    @Override
    public URL getUrl() {
        return url;
    }

}
    