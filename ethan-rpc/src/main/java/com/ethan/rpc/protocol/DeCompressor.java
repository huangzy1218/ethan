package com.ethan.rpc.protocol;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.model.FrameworkModel;

@SPI
public interface DeCompressor {

    DeCompressor NONE = Identity.IDENTITY;

    static DeCompressor getCompressor(FrameworkModel frameworkModel, String compressorStr) {
        if (null == compressorStr) {
            return null;
        }
        if (compressorStr.equals(Identity.MESSAGE_ENCODING)) {
            return NONE;
        }
        return frameworkModel.getExtensionLoader(DeCompressor.class).getExtension(compressorStr);
    }

    /**
     * Decompress payload.
     *
     * @param payloadByteArr Payload byte array
     * @return decompressed Payload byte array
     */
    byte[] decompress(byte[] payloadByteArr);

}
