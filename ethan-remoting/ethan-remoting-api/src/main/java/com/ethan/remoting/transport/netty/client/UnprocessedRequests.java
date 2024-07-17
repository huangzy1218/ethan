package com.ethan.remoting.transport.netty.client;

import com.ethan.remoting.exchange.Response;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class UnprocessedRequests {

    private static final Map<Long, CompletableFuture<Response>> UNPROCESSED_RESPONSE_FUTURES = new ConcurrentHashMap<>();

    public void put(long requestId, CompletableFuture<Response> future) {
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

    public void completeRequest(long requestId, Response response) {
        CompletableFuture<Response> future = UNPROCESSED_RESPONSE_FUTURES.remove(requestId);
        if (future != null) {
            future.complete(response);
            log.info("Request with ID {} completed successfully.", requestId);
        } else {
            log.warn("No unprocessed future found for request ID: {}", requestId);
        }
    }

}
