package com.ethan.rpc.protocol.compressor.support;

import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.rpc.protocol.compressor.Decompressor;

/**
 * Default compressor.
 *
 * @author Huang Z.Y.
 */
public class Identity implements Compressor, Decompressor {

    public static final String MESSAGE_ENCODING = "identity";

    public static final Identity IDENTITY = new Identity();

    @Override
    public String getMessageEncoding() {
        return MESSAGE_ENCODING;
    }

    @Override
    public byte[] compress(byte[] payloadByteArr) {
        return payloadByteArr;
    }

    @Override
    public byte[] decompress(byte[] payloadByteArr) {
        return payloadByteArr;
    }

}
