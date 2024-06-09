package com.ethan.serialize;

/**
 * Serialized runtime exceptions, internal flow,
 * will be converted into general exceptions and added to serialization tags when returning to rpc.
 *
 * @author Huang Z.Y.
 */
public class SerializationException extends Exception {

    private static final long serialVersionUID = -3160452149606778709L;

    public SerializationException(String msg) {
        super(msg);
    }

    public SerializationException(Throwable cause) {
        super(cause);
    }
}

    