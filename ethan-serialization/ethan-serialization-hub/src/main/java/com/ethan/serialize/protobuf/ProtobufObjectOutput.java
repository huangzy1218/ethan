package com.ethan.serialize.protobuf;

import com.ethan.serialize.Cleanable;
import com.ethan.serialize.ObjectOutput;
import com.google.protobuf.Message;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Protobuf object output implementation.
 *
 * @author Huang Z.Y.
 */
public class ProtobufObjectOutput implements ObjectOutput, Cleanable {

    private final OutputStream output;

    public ProtobufObjectOutput(OutputStream output) {
        this.output = output;
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        throw new UnsupportedOperationException("Use writeObject() for Protobuf serialization.");
    }

    @Override
    public void writeByte(byte v) throws IOException {
        output.write(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        output.write((v >> 8) & 0xFF);
        output.write(v & 0xFF);
    }

    @Override
    public void writeInt(int v) throws IOException {
        output.write((v >> 24) & 0xFF);
        output.write((v >> 16) & 0xFF);
        output.write((v >> 8) & 0xFF);
        output.write(v & 0xFF);
    }

    @Override
    public void writeLong(long v) throws IOException {
        output.write((int) (v >> 56) & 0xFF);
        output.write((int) (v >> 48) & 0xFF);
        output.write((int) (v >> 40) & 0xFF);
        output.write((int) (v >> 32) & 0xFF);
        output.write((int) (v >> 24) & 0xFF);
        output.write((int) (v >> 16) & 0xFF);
        output.write((int) (v >> 8) & 0xFF);
        output.write((int) v & 0xFF);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeInt(Float.floatToIntBits(v));
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeLong(Double.doubleToLongBits(v));
    }

    @Override
    public void writeUTF(String v) throws IOException {
        output.write(v.getBytes());
    }

    @Override
    public void writeBytes(byte[] v) throws IOException {
        output.write(v);
    }

    @Override
    public void writeBytes(byte[] v, int off, int len) throws IOException {
        output.write(v, off, len);
    }

    @Override
    public void flushBuffer() throws IOException {
        output.flush();
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        if (obj instanceof Message) {
            ((Message) obj).writeTo(output);
        } else {
            throw new IOException("Only Protobuf objects are supported.");
        }
    }

    @Override
    public void cleanup() {
        try {
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
}
    