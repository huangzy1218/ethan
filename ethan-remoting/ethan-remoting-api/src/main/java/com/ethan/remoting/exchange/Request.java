package com.ethan.remoting.exchange;

import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

import static com.ethan.common.constant.CommonConstants.HEARTBEAT_EVENT;

/**
 * Request.
 *
 * @author Huang Z.Y.
 */
@Data
@ToString
public class Request {

    private static final AtomicLong INVOKE_ID;

    static {
        long startId = ThreadLocalRandom.current().nextLong();
        INVOKE_ID = new AtomicLong(startId);
    }

    @Getter
    private final long id;
    private Object data;
    private String version;
    private boolean broken = false;
    private boolean event = false;


    public Request() {
        id = newId();
    }

    public Request(long id) {
        this.id = id;
    }

    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_EVENT == data;
    }


}
