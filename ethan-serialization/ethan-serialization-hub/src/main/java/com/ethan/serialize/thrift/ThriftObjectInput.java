package com.ethan.serialize.thrift;

import com.ethan.serialize.Cleanable;
import com.ethan.serialize.ObjectInput;
import org.apache.thrift.TBase;
import org.apache.thrift.TDeserializer;
import org.apache.thrift.protocol.TBinaryProtocol;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Thrift object input implementation.
 *
 * @author Huang Z.Y.
 */
public class ThriftObjectInput implements ObjectInput, Cleanable {

    private final InputStream input;

    public ThriftObjectInput(InputStream input) {
        this.input = input;
    }

    @Override
    public boolean readBool() throws IOException {
        byte[] buffer = new byte[1];
        input.read(buffer);
        return buffer[0] != 0;
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) input.read();
    }

    @Override
    public short readShort() throws IOException {
        byte[] buffer = new byte[2];
        input.read(buffer);
        return (short) ((buffer[0] << 8) | (buffer[1] & 0xFF));
    }

    @Override
    public int readInt() throws IOException {
        byte[] buffer = new byte[4];
        input.read(buffer);
        return (buffer[0] << 24) | (buffer[1] << 16) | (buffer[2] << 8) | (buffer[3] & 0xFF);
    }

    @Override
    public long readLong() throws IOException {
        byte[] buffer = new byte[8];
        input.read(buffer);
        return (long) (buffer[0] & 0xFF) << 56 | (long) (buffer[1] & 0xFF) << 48 |
                (long) (buffer[2] & 0xFF) << 40 | (long) (buffer[3] & 0xFF) << 32 |
                (long) (buffer[4] & 0xFF) << 24 | (long) (buffer[5] & 0xFF) << 16 |
                (long) (buffer[6] & 0xFF) << 8 | (buffer[7] & 0xFF);
    }

    @Override
    public float readFloat() throws IOException {
        return Float.intBitsToFloat(readInt());
    }

    @Override
    public double readDouble() throws IOException {
        return Double.longBitsToDouble(readLong());
    }

    @Override
    public String readUTF() throws IOException {
        int length = readInt();
        byte[] buffer = new byte[length];
        input.read(buffer);
        return new String(buffer);
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = readInt();
        byte[] buffer = new byte[length];
        input.read(buffer);
        return buffer;
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("Use readObject(Class<T> cls) to deserialize Protobuf message.");
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        try {
            TBase<?, ?> instance = (TBase<?, ?>) cls.newInstance();
            TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());
            deserializer.deserialize(instance, input.readAllBytes());
            return (T) instance;
        } catch (Exception e) {
            throw new IOException("Failed to deserialize Thrift object", e);
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        try {
            // Verify that the class is of TBase type and that all data structures in Thrift implement the TBase interface
            if (TBase.class.isAssignableFrom(cls)) {
                TBase<?, ?> instance = (TBase<?, ?>) cls.newInstance();
                TDeserializer deserializer = new TDeserializer(new TBinaryProtocol.Factory());

                // Bytes are read from the input stream and deserialized using Thrift.
                deserializer.deserialize(instance, input.readAllBytes());

                // Cast and return
                return (T) instance;
            } else {
                throw new IOException("Unsupported class type for Thrift deserialization");
            }
        } catch (Exception e) {
            throw new IOException("Failed to deserialize Thrift object", e);
        }
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
    