package com.ethan.rpc;

import lombok.Getter;
import lombok.Setter;

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
    public static final int BIZ_EXCEPTION = 3;


    private static final long serialVersionUID = 8350498255142199397L;


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

    public boolean isBiz() {
        return code == BIZ_EXCEPTION;
    }

}
