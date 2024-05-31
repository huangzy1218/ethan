package com.ethan.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Output;
import com.ethan.serialize.api.Cleanable;
import com.ethan.serialize.api.ObjectOutput;
import com.ethan.serialize.kryo.util.KryoThreadLocalUtils;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Kryo object output implementation.
 *
 * @author Huang Z.Y.
 */
public class KryoObjectOutput implements ObjectOutput, Cleanable {

    private final Kryo kryo;
    private final Output output;

    public KryoObjectOutput(OutputStream os) {
        kryo = KryoThreadLocalUtils.getKryo();
        this.output = new Output(os);
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeBytes(byte[] v) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void writeBytes(byte[] v, int off, int len) throws IOException {
        kryo.writeObject(output, v);
    }

    @Override
    public void flushBuffer() throws IOException {
        output.flush();
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        kryo.register(obj.getClass());
        kryo.writeObject(output, obj);
    }

    @Override
    public void cleanup() {
        output.close();
        KryoThreadLocalUtils.releaseKryo();
    }

}
