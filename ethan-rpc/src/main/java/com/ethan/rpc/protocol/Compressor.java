package com.ethan.rpc.protocol;

import com.ethan.rpc.model.FrameworkModel;

public interface Compressor {

    Compressor NONE = Identity.IDENTITY;

    static Compressor getCompressor(FrameworkModel frameworkModel, String compressorStr) {
        if (null == compressorStr) {
            return null;
        }
        if (compressorStr.equals(Identity.MESSAGE_ENCODING)) {
            return NONE;
        }
        return frameworkModel.getExtensionLoader(Compressor.class).getExtension(compressorStr);
    }

    /**
     * Compress payload.
     *
     * @param payloadByteArr Payload byte array
     * @return Compressed payload byte array
     */
    byte[] compress(byte[] payloadByteArr);

}
