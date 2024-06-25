package com.ethan.rpc.protocol.compressor;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.model.FrameworkModel;
import com.ethan.rpc.protocol.compressor.support.Identity;

/**
 * Compressor interface.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface Compressor extends MessageEncoding {

    Compressor NONE = Identity.IDENTITY;

    byte getContentTypeId();

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

    /**
     * Decompress payload.
     *
     * @param payloadByteArr Payload byte array
     * @return decompressed Payload byte array
     */
    byte[] decompress(byte[] payloadByteArr);

}
