package com.ethan.rpc;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;

/**
 * Custom RPC exception.
 *
 * @author Huang Z.Y.
 */
@Getter
@Setter
public class RpcException extends RuntimeException {

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;

    @Serial
    private static final long serialVersionUID = 2691386627L;


    /**
     * RpcException cannot be extended, use error code for exception type to keep compatibility.
     */
    private int code;

    public RpcException() {
        super();
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        super();
        this.code = code;
    }

}
