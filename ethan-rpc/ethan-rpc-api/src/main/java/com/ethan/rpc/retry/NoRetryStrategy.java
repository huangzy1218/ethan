package com.ethan.rpc.retry;

import com.ethan.rpc.AppResponse;

import java.util.concurrent.Callable;

/**
 * No retry.
 *
 * @author Huang Z.Y.
 */
public class NoRetryStrategy implements RetryStrategy {

    @Override
    public AppResponse doRetry(Callable<AppResponse> callable) throws Exception {
        return callable.call();
    }

}
