package com.ethan.remoting.exchange;

import lombok.Data;
import lombok.ToString;

import static com.ethan.common.constant.CommonConstants.HEARTBEAT_EVENT;

/**
 * Response.
 *
 * @author Huang Z.Y.
 */
@Data
@ToString
public class Response {

    /**
     * OK.
     */
    public static final byte OK = 20;

    /**
     * Serialization error.
     */
    public static final byte SERIALIZATION_ERROR = 25;

    /**
     * Client side timeout.
     */
    public static final byte CLIENT_TIMEOUT = 30;

    /**
     * Server side timeout.
     */
    public static final byte SERVER_TIMEOUT = 31;

    /**
     * Channel inactive, directly return the unfinished requests.
     */
    public static final byte CHANNEL_INACTIVE = 35;

    /**
     * Request format error.
     */
    public static final byte BAD_REQUEST = 40;

    /**
     * Response format error.
     */
    public static final byte BAD_RESPONSE = 50;

    /**
     * Service not found.
     */
    public static final byte SERVICE_NOT_FOUND = 60;

    /**
     * Service error.
     */
    public static final byte SERVICE_ERROR = 70;

    /**
     * Internal server error.
     */
    public static final byte SERVER_ERROR = 80;

    /**
     * Internal server error.
     */
    public static final byte CLIENT_ERROR = 90;

    /**
     * Server side thread pool exhausted and quick return.
     */
    public static final byte SERVER_THREADPOOL_EXHAUSTED_ERROR = 100;

    private long id = 0;
    private byte status = OK;
    private String errorMsg;
    private Object result;
    private String version;
    private boolean event = false;

    public Response() {
    }

    public Response(long id) {
        this.id = id;
    }

    public Response(long id, String version) {
        this.id = id;
        this.version = version;
    }

    public boolean isHeartbeat() {
        return event && HEARTBEAT_EVENT == result;
    }


}
