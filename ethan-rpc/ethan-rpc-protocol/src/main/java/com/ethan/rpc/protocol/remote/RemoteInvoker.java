package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.rpc.*;
import com.ethan.rpc.protocol.AbstractInvoker;
import com.ethan.rpc.protocol.local.NativeProtocol;
import com.github.rholder.retry.*;
import lombok.Setter;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Huang Z.Y.
 */
public class RemoteInvoker<T> extends AbstractInvoker<T> {

    private final String key;
    private final Map<String, Exporter<?>> exporterMap;
    @Setter
    private volatile Exporter<?> exporter = null;

    public RemoteInvoker(Class<T> type, URL url, String key, Map<String, Exporter<?>> exporterMap) {
        super(type, url);
        this.key = key;
        this.exporterMap = exporterMap;
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        // Build the Guava retries
        Retryer<Result> retryer = RetryerBuilder.<Result>newBuilder()
                // Retry condition: Retry when RpcException is caught
                .retryIfExceptionOfType(Exception.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .build();
        if (exporter == null) {
            exporter = NativeProtocol.getExporter(exporterMap, getUrl());
            if (exporter == null) {
                throw new RpcException("Service [" + key + "] not found.");
            }
        }
        // Solve local exposure, the server opens the token, and the client call fails.
        Invoker<?> invoker = exporter.getInvoker();
        Callable<Result> retryableTask = () -> invoker.invoke(invocation);

        try {
            return retryer.call(retryableTask);
        } catch (RetryException | ExecutionException e) {
            throw new RpcException("Retry call failure", e);
        }
    }

}
