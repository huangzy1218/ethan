package com.ethan.remoting.tansport.netty;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class RpcMessage implements Serializable {

    @Serial
    private static final long serialVersionUID = 2691386627L;

    /**
     * RPC message type.
     */
    private byte messageType;
    /**
     * Serialization type.
     */
    private String codec;
    /**
     * Compress type.
     */
    private byte compress;
    /**
     * Request ID.
     */
    private int requestId;
    /**
     * Request data.
     */
    private Object data;

}
