package com.ethan.remoting.compressor.support;

import com.ethan.common.enumeration.CompressType;
import com.ethan.remoting.compressor.Compressor;

/**
 * Default compressor.
 *
 * @author Huang Z.Y.
 */
public class Identity implements Compressor {

    public static final String MESSAGE_ENCODING = "identity";

    public static final Identity IDENTITY = new Identity();

    @Override
    public String getMessageEncoding() {
        return MESSAGE_ENCODING;
    }

    @Override
    public byte getContentTypeId() {
        return CompressType.getCode(MESSAGE_ENCODING);
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
