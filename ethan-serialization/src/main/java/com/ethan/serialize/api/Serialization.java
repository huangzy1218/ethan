package com.ethan.serialize.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Specify the serialization strategy interface of the serializer.
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
