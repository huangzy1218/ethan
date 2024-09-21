package com.ethan.rpc.retry;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.AppResponse;

import java.util.concurrent.Callable;

/**
 * Retry strategy.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface RetryStrategy {

    /**
     * Retry.
     *
     * @param callable Callable response.
     * @return Response
     */
    AppResponse doRetry(Callable<AppResponse> callable) throws Exception;

}
