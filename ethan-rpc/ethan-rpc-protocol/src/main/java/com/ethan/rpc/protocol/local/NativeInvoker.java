package com.ethan.rpc.protocol.local;

import com.ethan.common.URL;
import com.ethan.common.url.component.ServiceAddressURL;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.AbstractInvoker;
import com.ethan.rpc.support.RpcUtils;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * @author Huang Z.Y.
 */
public class NativeInvoker<T> extends AbstractInvoker<T> {

    private volatile Exporter<?> exporter;

    private volatile URL consumerUrl = null;

    public NativeInvoker(Class<T> type, URL url, Exporter<?> exporter) {
        super(type, url);
        this.exporter = exporter;
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        // Solve local exposure, the server opens the token, and the client call fails.
        Invoker<?> invoker = exporter.getInvoker();
        URL serverURL = invoker.getUrl();
        if (consumerUrl == null) {
            // no need to sync, multi-objects is acceptable and will be gc-ed.
            consumerUrl =
                    new ServiceAddressURL(serverURL.getUrlAddress(), serverURL.getUrlParam(), getUrl());
        }

        int timeout = RpcUtils.calculateTimeout(consumerUrl, DEFAULT_TIMEOUT);
        if (timeout <= 0) {
            return AsyncRpcResult.newDefaultAsyncResult(
                    new RpcException("No time left for making the following call: " + invocation.getServiceName() + "."
                            + RpcUtils.getMethodName(invocation) + ", terminate directly."), invocation);
        }
        invocation.setAttachment(TIMEOUT_KEY, String.valueOf(timeout));

        if (isAsync(invoker.getUrl(), getUrl())) {
            ((RpcInvocation) invocation).setInvokeMode(InvokeMode.ASYNC);
            ExecutorService executor = ExecutorService
            CompletableFuture<AppResponse> appResponseFuture = CompletableFuture.supplyAsync(
                    () -> {
                        // clear thread local before child invocation, prevent context pollution
                        InternalThreadLocalMap originTL = InternalThreadLocalMap.getAndRemove();
                        try {
                            RpcContext.getServiceContext().setRemoteAddress(LOCALHOST_VALUE, 0);
                            RpcContext.getServiceContext().setRemoteApplicationName(getUrl().getApplication());
                            Result result = invoker.invoke(copiedInvocation);
                            if (result.hasException()) {
                                AppResponse appResponse = new AppResponse(result.getException());
                                appResponse.setObjectAttachments(new HashMap<>(result.getObjectAttachments()));
                                return appResponse;
                            } else {
                                rebuildValue(invocation, invoker, result);
                                AppResponse appResponse = new AppResponse(result.getValue());
                                appResponse.setObjectAttachments(new HashMap<>(result.getObjectAttachments()));
                                return appResponse;
                            }
                        } finally {
                            InternalThreadLocalMap.set(originTL);
                        }
                    },
                    executor);
            // save for 2.6.x compatibility, for example, TraceFilter in Zipkin uses com.alibaba.xxx.FutureAdapter
            if (setFutureWhenSync || ((RpcInvocation) invocation).getInvokeMode() != InvokeMode.SYNC) {
                FutureContext.getContext().setCompatibleFuture(appResponseFuture);
            }
            AsyncRpcResult result = new AsyncRpcResult(appResponseFuture, copiedInvocation);
            result.setExecutor(executor);
            return result;
        } else {
            Result result;
            // clear thread local before child invocation, prevent context pollution
            InternalThreadLocalMap originTL = InternalThreadLocalMap.getAndRemove();
            try {
                RpcContext.getServiceContext().setRemoteAddress(LOCALHOST_VALUE, 0);
                RpcContext.getServiceContext().setRemoteApplicationName(getUrl().getApplication());
                result = invoker.invoke(copiedInvocation);
            } finally {
                InternalThreadLocalMap.set(originTL);
            }
            CompletableFuture<AppResponse> future = new CompletableFuture<>();
            AppResponse rpcResult = new AppResponse(copiedInvocation);
            if (result instanceof AsyncRpcResult) {
                result.whenCompleteWithContext((r, t) -> {
                    if (t != null) {
                        rpcResult.setException(t);
                    } else {
                        if (r.hasException()) {
                            rpcResult.setException(r.getException());
                        } else {
                            Object rebuildValue = rebuildValue(invocation, invoker, r.getValue());
                            rpcResult.setValue(rebuildValue);
                        }
                    }
                    rpcResult.setObjectAttachments(new HashMap<>(r.getObjectAttachments()));
                    future.complete(rpcResult);
                });
            } else {
                if (result.hasException()) {
                    rpcResult.setException(result.getException());
                } else {
                    Object rebuildValue = rebuildValue(invocation, invoker, result.getValue());
                    rpcResult.setValue(rebuildValue);
                }
                rpcResult.setObjectAttachments(new HashMap<>(result.getObjectAttachments()));
                future.complete(rpcResult);
            }
            return new AsyncRpcResult(future, invocation);
        }
    }


    private boolean isAsync(URL remoteUrl, URL localUrl) {
        if (localUrl.hasParameter(ASYNC_KEY)) {
            return localUrl.getParameter(ASYNC_KEY, false);
        }
        return remoteUrl.getParameter(ASYNC_KEY, false);
    }

}
