package com.ethan.serialize.thrift;

import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Thrift serialization implementation.
 *
 * @author Huang Z.Y.
 */
public class ThriftSerialization implements Serialization {

    private static final long serialVersionUID = 3033787999037024736L;

    @Override
    public byte getContentTypeId() {
        return 6;  // Assign an arbitrary ID for Thrift serialization
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new ThriftObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new ThriftObjectInput(input);
    }

}