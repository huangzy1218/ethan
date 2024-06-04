package com.ethan.remoting.exchange;

import lombok.Data;
import lombok.ToString;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Request.
 *
 * @author Huang Z.Y.
 */
@Data
@ToString
public class Request {

    private static final AtomicLong INVOKE_ID;

    private final long id;

    private Object data;


    static {
        long startID = ThreadLocalRandom.current().nextLong();
        INVOKE_ID = new AtomicLong(startID);
    }

    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }

    public Request() {
        id = newId();
    }

    public Request(long id) {
        this.id = id;
    }

}
