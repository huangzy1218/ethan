package com.ethan.rpc.protocol;

/**
 * Default compressor.
 *
 * @author Huang Z.Y.
 */
public class Identity implements Compressor, DeCompressor {

    public static final String MESSAGE_ENCODING = "identity";
    
    public static final Identity IDENTITY = new Identity();

    @Override
    public byte[] compress(byte[] payloadByteArr) {
        return payloadByteArr;
    }

    @Override
    public byte[] decompress(byte[] payloadByteArr) {
        return payloadByteArr;
    }

}
