package com.ethan.rpc.protocol.injvm;

import com.ethan.common.URL;
import com.ethan.common.threadpool.AdaptiveExecuteService;
import com.ethan.common.util.ExecutorUtils;
import com.ethan.model.ApplicationModel;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.AbstractInvoker;
import com.ethan.rpc.support.RpcUtils;
import lombok.Setter;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * Invoker in local JVM.
 *
 * @author Huang Z.Y.
 */
public class InjvmInvoker<T> extends AbstractInvoker<T> {

    @Setter
    private Exporter<?> exporter;

    public InjvmInvoker(Class<T> type, URL url, Exporter<T> exporter) {
        super(type, url);
        this.exporter = exporter;
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        // Solve local exposure, the server opens the token, and the client call fails.
        Invoker<?> invoker = exporter.getInvoker();
        int timeout = (Integer) ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.timeout", DEFAULT_TIMEOUT);
        if (timeout <= 0) {
            return AsyncRpcResult.newDefaultAsyncResult(
                    new RpcException("No time left for making the following call: " + invocation.getServiceName() + "."
                            + RpcUtils.getMethodName(invocation) + ", terminate directly."), invocation);
        }
        invocation.setAttachment(TIMEOUT_KEY, String.valueOf(timeout));

        if (isAsync(invoker.getUrl(), getUrl())) {
            ((RpcInvocation) invocation).setInvokeMode(InvokeMode.ASYNC);
            ExecutorService executor = AdaptiveExecuteService.newDefaultExecutor(ExecutorUtils.setThreadName(getUrl(),
                    SERVER_THREAD_POOL_NAME));
            CompletableFuture<AppResponse> appResponseFuture = CompletableFuture.supplyAsync(
                    () -> {
                        Result result = invoker.invoke(invocation);
                        if (result.hasException()) {
                            return new AppResponse(result.getException());
                        } else {
                            return new AppResponse(result.getValue());
                        }
                    }, executor);
            AsyncRpcResult result = new AsyncRpcResult(appResponseFuture, invocation);
            result.setExecutor(executor);
            return result;
        } else {
            Result result;
            // Clear thread local before child invocation, prevent context pollution
            result = invoker.invoke(invocation);
            CompletableFuture<AppResponse> future = new CompletableFuture<>();
            AppResponse rpcResult = new AppResponse(invocation);

            if (result.hasException()) {
                rpcResult.setException(result.getException());
            } else {
                rpcResult.setValue(result.getValue());
            }
            future.complete(rpcResult);
            return new AsyncRpcResult(future, invocation);
        }
    }


    private boolean isAsync(URL remoteUrl, URL localUrl) {
        if (localUrl.hasParameter(ASYNC_KEY)) {
            return localUrl.getParameter(ASYNC_KEY, false);
        }
        return remoteUrl.getParameter(ASYNC_KEY, false);
    }

    @Override
    public boolean isAvailable() {
        if (exporter == null) {
            return false;
        } else {
            return super.isAvailable();
        }
    }

}
