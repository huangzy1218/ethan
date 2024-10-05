package com.ethan.serialize.protobuf;

import com.ethan.serialize.Cleanable;
import com.ethan.serialize.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

/**
 * Protobuf object input implementation.
 *
 * @author Huang Z.Y.
 */
public class ProtobufObjectInput implements ObjectInput, Cleanable {

    private final InputStream input;

    public ProtobufObjectInput(InputStream input) {
        this.input = input;
    }

    @Override
    public boolean readBool() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) input.read();
    }

    @Override
    public short readShort() throws IOException {
        return (short) ((input.read() << 8) | input.read());
    }

    @Override
    public int readInt() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public long readLong() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public float readFloat() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public double readDouble() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public String readUTF() throws IOException {
        throw new UnsupportedOperationException("Use readObject() to deserialize Protobuf message.");
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = input.read();
        byte[] buffer = new byte[length];
        input.read(buffer);
        return buffer;
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Use readObject(Class<T> cls) for Protobuf objects.");
    }

    @Override
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        try {
            Method parseMethod = cls.getMethod("parseFrom", InputStream.class);
            return (T) parseMethod.invoke(null, input);
        } catch (Exception e) {
            throw new IOException("Failed to deserialize Protobuf object", e);
        }
    }

    @Override
    public <T> T readObject(Class<T> cls, java.lang.reflect.Type type) throws IOException, ClassNotFoundException {
        return readObject(cls);
    }

    @Override
    public void cleanup() {
        try {
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
    