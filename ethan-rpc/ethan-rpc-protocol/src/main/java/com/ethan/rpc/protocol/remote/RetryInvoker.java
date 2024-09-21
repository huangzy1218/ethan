package com.ethan.rpc.protocol.remote;

import com.ethan.common.URL;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Result;
import com.ethan.rpc.RpcException;
import com.github.rholder.retry.*;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * @author Huang Z.Y.
 */
public class RetryInvoker<T> extends RemoteInvoker<T> {

    public RetryInvoker(Class<T> type, URL url, String key, Map<String, Exporter<?>> exporterMap) {
        super(type, url, key, exporterMap);
    }

    @Override
    public Result doInvoke(Invocation invocation) throws RpcException {
        // Build the Guava retries
        Retryer<Result> retryer = RetryerBuilder.<Result>newBuilder()
                .retryIfExceptionOfType(RpcException.class)
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
                .withWaitStrategy(WaitStrategies.fixedWait(2, TimeUnit.SECONDS))
                .build();

        Callable<Result> retryableTask = () -> super.invoke(invocation);

        try {
            return retryer.call(retryableTask);
        } catch (RetryException | ExecutionException e) {
            throw new RpcException("Retry call failure", e);
        }
    }

}
    