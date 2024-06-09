package com.ethan.rpc;

import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request message.
 *
 * @author Huang Z.Y.
 */
@Data
@Builder
public class Message implements Serializable {

    @Serial
    private static final long serialVersionUID = 2691386627L;

    /**
     * RPC message type.
     */
    private byte messageType;
    /**
     * Serialization type.
     */
    private byte codec;
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
