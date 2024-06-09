package com.ethan.rpc;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * Request message.
 *
 * @author Huang Z.Y.
 */
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
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
