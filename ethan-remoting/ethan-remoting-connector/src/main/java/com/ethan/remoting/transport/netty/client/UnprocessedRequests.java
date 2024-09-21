package com.ethan.remoting.tansport.netty.client;

import com.ethan.remoting.exchange.Response;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Unprocessed requests by the server.
 *
 * @author Huang Z.Y.
 */
@Component
public class UnprocessedRequests {

    private static final Map<String, CompletableFuture<Response>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(String requestId, CompletableFuture<Response> future) {
        UNPROCESSED_RESPONSE_FUTURES.put(requestId, future);
    }

    public void complete(Response response) {
        CompletableFuture<Response> future = UNPROCESSED_RESPONSE_FUTURES.remove(response.getId());
        if (null != future) {
            future.complete(response);
        } else {
            throw new IllegalStateException();
        }
    }

}
