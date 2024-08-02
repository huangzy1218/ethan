package com.ethan.remoting.transport;

import com.ethan.common.URL;
import com.ethan.common.extension.ExtensionLoader;
import com.ethan.common.util.UrlUtils;
import com.ethan.model.ApplicationModel;
import com.ethan.rpc.compressor.Compressor;
import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;
import lombok.extern.slf4j.Slf4j;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Default support of codec.
 *
 * @author Huang Z.Y.
 */
@Slf4j
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

    public static Serialization getSerialization(URL url) {
        return ApplicationModel.defaultModel()
                .getExtensionLoader(Serialization.class)
                .getExtension(UrlUtils.serializationOrDefault(url));
    }

    public static ObjectInput deserialize(InputStream is, byte proto) throws IOException {
        Serialization s = getSerialization(proto);
        return s.deserialize(is);
    }

    /**
     * Get the null object serialize result byte[] of Serialization.
     *
     * @param s Serialization Instances
     * @return Serialize result of null object
     */
    public static byte[] getNullBytesOf(Serialization s) {
        // Pre-generated Null object bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] nullBytes = new byte[0];
        try {
            ObjectOutput out = s.serialize(baos);
            out.writeObject(null);
            out.flushBuffer();
            nullBytes = baos.toByteArray();
            baos.close();
        } catch (Exception e) {
            log.warn(
                    "Serialization extension " + s.getClass().getName()
                            + " not support serializing null object, return an empty bytes instead.");
        }
        return nullBytes;
    }

}
    