package com.ethan.serialize.protobuf;

import com.ethan.serialize.ObjectInput;
import com.ethan.serialize.ObjectOutput;
import com.ethan.serialize.Serialization;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Protobuf serialization implementation.
 *
 * @author Huang Z.Y.
 */
public class ProtobufSerialization implements Serialization {

    private static final long serialVersionUID = 1L;

    @Override
    public byte getContentTypeId() {
        return 5; // Assign an ID for Protobuf serialization.
    }

    @Override
    public ObjectOutput serialize(OutputStream output) throws IOException {
        return new ProtobufObjectOutput(output);
    }

    @Override
    public ObjectInput deserialize(InputStream input) throws IOException {
        return new ProtobufObjectInput(input);
    }
    
}
    