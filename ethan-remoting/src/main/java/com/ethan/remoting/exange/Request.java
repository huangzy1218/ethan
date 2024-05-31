package com.ethan.remoting.exange;

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

    private final long mId;


    static {
        long startID = ThreadLocalRandom.current().nextLong();
        INVOKE_ID = new AtomicLong(startID);
    }

    private static long newId() {
        // getAndIncrement() When it grows to MAX_VALUE, it will grow to MIN_VALUE, and the negative can be used as ID
        return INVOKE_ID.getAndIncrement();
    }

    public Request() {
        mId = newId();
    }

    public Request(long id) {
        mId = id;
    }

}
