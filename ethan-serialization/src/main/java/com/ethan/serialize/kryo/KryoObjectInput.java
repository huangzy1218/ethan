package com.ethan.serialize.kryo;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.ethan.serialize.api.Cleanable;
import com.ethan.serialize.api.ObjectInput;
import com.ethan.serialize.kryo.util.KryoThreadLocalUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * Kryo object input implementation.
 *
 * @author Huang Z.Y.
 */
public class KryoObjectInput implements ObjectInput, Cleanable {

    private final Kryo kryo;
    private final Input input;

    public KryoObjectInput(InputStream is) {
        kryo = KryoThreadLocalUtils.getKryo();
        this.input = new Input(is);
    }

    @Override
    public boolean readBool() throws IOException {
        return input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return input.readByte();
    }

    @Override
    public short readShort() throws IOException {
        return input.readShort();
    }

    @Override
    public int readInt() throws IOException {
        return input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return input.readLong();
    }

    @Override
    public float readFloat() throws IOException {
        return input.readFloat();
    }

    @Override
    public double readDouble() throws IOException {
        return input.readDouble();
    }

    @Override
    public String readUTF() throws IOException {
        return kryo.readObject(input, String.class);
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = input.readInt();
        return input.readBytes(length);
    }

    @Override
    @Deprecated
    public Object readObject() throws IOException, ClassNotFoundException {
        return kryo.readObject(input, Object.class);
    }

    @Override
    public <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException {
        return kryo.readObject(input, cls);
    }

    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        return readObject(cls);
    }

    @Override
    public void cleanup() {
        input.close();
        KryoThreadLocalUtils.releaseKryo();
    }

}

