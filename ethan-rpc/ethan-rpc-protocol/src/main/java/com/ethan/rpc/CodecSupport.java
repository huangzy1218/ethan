package com.ethan.rpc;

import com.ethan.common.extension.ExtensionLoader;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.protocol.compressor.Compressor;
import com.ethan.serialize.Serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Default support of codec.
 *
 * @author Huang Z.Y.
 */
public class CodecSupport {

    private static Map<Byte, Serialization> ID_SERIALIZATION_MAP = new HashMap<>();
    private static Map<Byte, Compressor> ID_COMPRESSOR_MAP = new HashMap<>();


    static {
        ExtensionLoader<Serialization> serializationExtensionLoader =
                ApplicationModel.defaultModel().getExtensionLoader(Serialization.class);
        Set<String> serializationSupportedExtensions = serializationExtensionLoader.getSupportedExtensions();
        for (String name : serializationSupportedExtensions) {
            Serialization serialization = serializationExtensionLoader.getExtension(name);
            byte idByte = serialization.getContentTypeId();
            ID_SERIALIZATION_MAP.put(idByte, serialization);
        }
        ExtensionLoader<Compressor> compressorExtensionLoader =
                ApplicationModel.defaultModel().getExtensionLoader(Compressor.class);
        Set<String> compressorSupportedExtensions = compressorExtensionLoader.getSupportedExtensions();
        for (String name : compressorSupportedExtensions) {
            Compressor compressor = compressorExtensionLoader.getExtension(name);
            byte idByte = compressor.getContentTypeId();
            ID_COMPRESSOR_MAP.put(idByte, compressor);
        }
    }

    public static Compressor getCompressor(byte id) {
        return ID_COMPRESSOR_MAP.get(id);
    }

    public static Serialization getSerialization(byte id) {
        return ID_SERIALIZATION_MAP.get(id);
    }

}
    