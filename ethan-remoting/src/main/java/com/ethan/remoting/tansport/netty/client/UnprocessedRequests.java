package com.ethan.remoting.tansport.netty.client;

import com.ethan.rpc.AppResponse;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unprocessed requests by the server.
 *
 * @author Huang Z.Y.
 */
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<AppResponse>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<AppResponse> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(AppResponse response) {
        CompletableFuture<AppResponse> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getRequestId());
        if (null != future) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

}
