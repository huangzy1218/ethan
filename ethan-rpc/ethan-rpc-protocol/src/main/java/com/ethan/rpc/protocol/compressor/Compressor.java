package com.ethan.rpc.protocol.compressor;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.protocol.compressor.support.Identity;

/**
 * Compressor interface.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface Compressor extends MessageEncoding {

    Compressor NONE = Identity.IDENTITY;

    static Compressor getCompressor(ApplicationModel ApplicationModel, String compressorStr) {
        if (null == compressorStr) {
            return null;
        }
        if (compressorStr.equals(Identity.MESSAGE_ENCODING)) {
            return NONE;
        }
        return ApplicationModel.getExtensionLoader(Compressor.class).getExtension(compressorStr);
    }

    byte getContentTypeId();

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
