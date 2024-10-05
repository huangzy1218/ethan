package com.ethan.serialize.thrift;

import com.ethan.serialize.Cleanable;
import com.ethan.serialize.ObjectOutput;
import org.apache.thrift.TBase;
import org.apache.thrift.TException;
import org.apache.thrift.TSerializer;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.transport.TTransportException;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Thrift object output implementation.
 *
 * @author Huang Z.Y.
 */
public class ThriftObjectOutput implements ObjectOutput, Cleanable {

    private final OutputStream output;

    public ThriftObjectOutput(OutputStream output) {
        this.output = output;
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        output.write(v ? 1 : 0);
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
        output.write((int) (v) & 0xFF);
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
        byte[] bytes = v.getBytes();
        writeInt(bytes.length);
        output.write(bytes);
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
        if (obj instanceof TBase) {
            TSerializer serializer = null;
            try {
                serializer = new TSerializer(new TBinaryProtocol.Factory());
            } catch (TTransportException e) {
                throw new RuntimeException(e);
            }
            byte[] serialized = new byte[0];
            try {
                serialized = serializer.serialize((TBase<?, ?>) obj);
            } catch (TException e) {
                throw new RuntimeException(e);
            }
            output.write(serialized);
        } else {
            throw new IOException("Only Thrift objects are supported.");
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
    