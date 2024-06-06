package com.ethan.rpc.protocol.compressor.support;

import com.ethan.rpc.RpcException;
import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.rpc.protocol.compressor.Decompressor;

import java.io.IOException;

/**
 * Snappy compressor, Provide high-speed compression speed and reasonable compression ratio.
 *
 * @author Huang Z.Y.
 */
public class Snappy implements Compressor, Decompressor {

    public static final String SNAPPY = "snappy";

    @Override
    public String getMessageEncoding() {
        return SNAPPY;
    }

    @Override
    public byte[] compress(byte[] payloadByteArr) throws RpcException {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        try {
            return org.xerial.snappy.Snappy.compress(payloadByteArr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public byte[] decompress(byte[] payloadByteArr) {
        if (null == payloadByteArr || 0 == payloadByteArr.length) {
            return new byte[0];
        }

        try {
            return org.xerial.snappy.Snappy.uncompress(payloadByteArr);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
