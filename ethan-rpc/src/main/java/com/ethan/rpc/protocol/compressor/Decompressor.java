package com.ethan.rpc.protocol.compressor;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.model.FrameworkModel;
import com.ethan.rpc.protocol.compressor.support.Identity;

/**
 * Decompressor interface.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface Decompressor {

    Decompressor NONE = Identity.IDENTITY;

    static Decompressor getCompressor(FrameworkModel frameworkModel, String compressorStr) {
        if (null == compressorStr) {
            return null;
        }
        if (compressorStr.equals(Identity.MESSAGE_ENCODING)) {
            return NONE;
        }
        return frameworkModel.getExtensionLoader(Decompressor.class).getExtension(compressorStr);
    }

    /**
     * Decompress payload.
     *
     * @param payloadByteArr Payload byte array
     * @return decompressed Payload byte array
     */
    byte[] decompress(byte[] payloadByteArr);

}
