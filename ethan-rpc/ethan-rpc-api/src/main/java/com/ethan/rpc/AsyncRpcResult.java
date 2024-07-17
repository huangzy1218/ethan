package com.ethan.rpc;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * This class represents an unfinished RPC call, it will hold some context information for this call, for example RpcContext and Invocation,
 * so that when the call finishes and the result returns, it can guarantee all the contexts being recovered as the same as when the call was made
 * before any callback is invoked.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class AsyncRpcResult implements Result {

    private final Invocation invocation;
    private final CompletableFuture<AppResponse> responseFuture;
    private final boolean async;
    private Executor executor;

    public AsyncRpcResult(CompletableFuture<AppResponse> future, Invocation invocation) {
        this.responseFuture = future;
        this.invocation = invocation;
        RpcInvocation rpcInvocation = (RpcInvocation) invocation;
        async = InvokeMode.SYNC != rpcInvocation.getInvokeMode();
    }

    private static Result createDefaultValue() {
        return new AppResponse();
    }

    public static AsyncRpcResult newDefaultAsyncResult(Throwable t, Invocation invocation) {
        return newDefaultAsyncResult(null, t, invocation);
    }

    public static AsyncRpcResult newDefaultAsyncResult(Invocation invocation) {
        return newDefaultAsyncResult(null, null, invocation);
    }

    public static AsyncRpcResult newDefaultAsyncResult(Object value, Throwable t, Invocation invocation) {
        CompletableFuture<AppResponse> future = new CompletableFuture<>();
        AppResponse result = new AppResponse(invocation);
        if (t != null) {
            result.setException(t);
        } else {
            result.setValue(value);
        }
        future.complete(result);
        return new AsyncRpcResult(future, invocation);
    }

    @Override
    public Object getValue() {
        return getAppResponse().getValue();
    }

    @Override
    public void setValue(Object value) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setValue(value);
            } else {
                AppResponse appResponse = new AppResponse(invocation);
                appResponse.setValue(value);
                responseFuture.complete(appResponse);
            }
        } catch (Exception e) {
            // This should not happen in normal request process
            log.error("Get exception when trying to fetch the underlying result.");
            throw new RpcException(e);
        }
    }

    public void setExecutor(Executor executor) {
        this.executor = executor;
    }

    @Override
    public Throwable getException() {
        return getAppResponse().getException();
    }

    @Override
    public void setException(Throwable t) {
        try {
            if (responseFuture.isDone()) {
                responseFuture.get().setException(t);
            } else {
                AppResponse appResponse = new AppResponse(invocation);
                appResponse.setException(t);
                responseFuture.complete(appResponse);
            }
        } catch (Exception e) {
            log.error("Get exception when trying to fetch the underlying result.");
            throw new RpcException(e);
        }
    }

    @Override
    public boolean hasException() {
        return getAppResponse().hasException();
    }

    @Override
    public Result get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        long deadline = System.nanoTime() + unit.toNanos(timeout);

        while (!responseFuture.isDone()) {
            long restTime = deadline - System.nanoTime();
            if (restTime <= 0) {
                throw new TimeoutException("Timeout after " + unit.toMillis(timeout) + "ms waiting for result.");
            }
            try {
                return responseFuture.get(restTime, TimeUnit.NANOSECONDS);
            } catch (TimeoutException e) {
                // Ignore
            }
        }

        return responseFuture.get();
    }

    public Result getAppResponse() {
        try {
            if (responseFuture.isDone()) {
                return responseFuture.get();
            }
        } catch (Exception e) {
            log.error("Get exception when trying to fetch the underlying result.");
            throw new RpcException(e);
        }
        return createDefaultValue();
    }

}
    