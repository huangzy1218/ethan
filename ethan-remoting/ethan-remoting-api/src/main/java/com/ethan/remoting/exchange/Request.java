package com.ethan.remoting.exchange;

import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.util.IdUtil;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

import static com.ethan.common.constant.CommonConstants.HEARTBEAT_EVENT;

/**
 * Request.
 *
 * @author Huang Z.Y.
 */
@Data
@ToString
public class Request {

    private static final Snowflake SNOWFLAKE_ID_WORKER;

    static {
        SNOWFLAKE_ID_WORKER = IdUtil.getSnowflake();
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
        return SNOWFLAKE_ID_WORKER.nextId();
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_EVENT == data;
    }

}
