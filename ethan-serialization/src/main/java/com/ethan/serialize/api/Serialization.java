package com.ethan.serialize.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 指定序列化器的序列化策略接口.
 *
 * @author Huang Z.Y.
 */
public interface Serialization {

    /**
     * Get the serialization implementation instance.
     *
     * @param output Underlying output stream
     * @return Serializer
     * @throws IOException If an I/O error occurs
     */
    ObjectOutput serialize(OutputStream output) throws IOException;

    /**
     * Get the deserialization implementation instance.
     *
     * @param input Underlying input stream
     * @return Deserializer
     * @throws IOException If an I/O error occurs
     */
    ObjectInput deserialize(InputStream input) throws IOException;

}
