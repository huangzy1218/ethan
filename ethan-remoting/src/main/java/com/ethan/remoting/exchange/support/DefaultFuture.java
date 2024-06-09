package com.ethan.remoting.exchange.support;

import com.ethan.remoting.Channel;
import com.ethan.remoting.RemotingException;
import com.ethan.remoting.exchange.Request;
import com.ethan.remoting.exchange.Response;
import com.ethan.serialize.SerializationException;
import lombok.Getter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.*;

import static com.ethan.rpc.Constants.DEFAULT_TIMEOUT;
import static com.ethan.rpc.Constants.TIMEOUT_KEY;

/**
 * Custom future.
 *
 * @author Huang Z.Y.
 */
public class DefaultFuture extends CompletableFuture<Object> {

    private long id;
    private static final Map<Long, DefaultFuture> FUTURES = new ConcurrentHashMap<>();

    @Getter
    private final Channel channel;

    private final Request request;

    private volatile long sent;


    private static final ScheduledExecutorService TIMEOUT_EXECUTOR =
            Executors.newScheduledThreadPool(1);
    @Getter
    private final int timeout;
    private static final Map<Long, Channel> CHANNELS = new ConcurrentHashMap<>();
    private final long start = System.currentTimeMillis();


    private DefaultFuture(Channel channel, Request request, int timeout) {
        this.channel = channel;
        this.request = request;
        this.id = request.getId();
        FUTURES.put(id, this);
        CHANNELS.put(id, channel);
        this.timeout = timeout > 0 ? timeout : channel.getUrl().getPositiveParameter(TIMEOUT_KEY, DEFAULT_TIMEOUT);
        // Schedule timeout check
        TIMEOUT_EXECUTOR.schedule(() -> timeoutCheck(this), this.timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * Init a DefaultFuture.<br/>
     * 1. init a DefaultFuture<br/>
     * 2. timeout check<br/>
     *
     * @param channel Channel
     * @param request The request
     * @return A new DefaultFuture
     */
    public static DefaultFuture newFuture(Channel channel, Request request, int timeout) {
        return new DefaultFuture(channel, request, timeout);
    }

    /**
     * check time out of the future
     */
    private static void timeoutCheck(DefaultFuture future) {
        if (future == null || future.isDone()) {
            return;
        }
        TimeoutCheckTask task = new TimeoutCheckTask(future.getId());
        task.run();
    }

    private static class TimeoutCheckTask {

        private final Long requestID;

        TimeoutCheckTask(Long requestID) {
            this.requestID = requestID;
        }

        public void run() {
            DefaultFuture future = DefaultFuture.getFuture(requestID);
            if (future == null || future.isDone()) {
                return;
            }


            notifyTimeout(future);
        }

        private void notifyTimeout(DefaultFuture future) {
            // Create exception response.
            Response timeoutResponse = new Response(future.getId());
            // Set timeout status
            timeoutResponse.setStatus(future.isSent() ? Response.SERVER_TIMEOUT : Response.CLIENT_TIMEOUT);
            timeoutResponse.setErrorMsg(future.getTimeoutMessage(true));
            // Handle response
            DefaultFuture.received(future.getChannel(), timeoutResponse);
        }
    }

    private boolean isSent() {
        return sent > 0;
    }

    private void doSent() {
        sent = System.currentTimeMillis();
    }

    public static void sent(Channel channel, Request request) {
        DefaultFuture future = FUTURES.get(request.getId());
        if (future != null) {
            future.doSent();
        }
    }

    public static void received(Channel channel, Response response) {
        try {
            DefaultFuture future = FUTURES.remove(response.getId());
            if (future != null) {
                future.doReceived(response);
            }
        } finally {
            CHANNELS.remove(response.getId());
        }
    }

    private void doReceived(Response res) {
        if (res == null) {
            throw new IllegalStateException("response cannot be null");
        }
        if (res.getStatus() == Response.OK) {
            this.complete(res.getResult());
        } else if (res.getStatus() == Response.CLIENT_TIMEOUT || res.getStatus() == Response.SERVER_TIMEOUT) {
            this.completeExceptionally(
                    new TimeoutException(res.getErrorMsg()));
        } else if (res.getStatus() == Response.SERIALIZATION_ERROR) {
            this.completeExceptionally(new SerializationException(res.getErrorMsg()));
        } else {
            this.completeExceptionally(new RemotingException(channel, res.getErrorMsg()));
        }
    }

    private String getTimeoutMessage(boolean scan) {
        long nowTimestamp = System.currentTimeMillis();
        return (sent > 0 && sent - start < timeout
                ? "Waiting server-side response timeout"
                : "Sending request timeout in client-side")
                + (scan ? " by scan timer" : "") + ". start time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(start))) + ", end time: "
                + (new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date(nowTimestamp))) + ","
                + (sent > 0
                ? " client elapsed: " + (sent - start) + " ms, server elapsed: " + (nowTimestamp - sent)
                : " elapsed: " + (nowTimestamp - start))
                + " ms, timeout: "
                + timeout + " ms, request: " + request
                + ", channel: " + channel.getLocalAddress()
                + " -> " + channel.getRemoteAddress();
    }

    // todo
    private long getId() {
        return id;
    }

    public static DefaultFuture getFuture(long id) {
        return FUTURES.get(id);
    }

}
    