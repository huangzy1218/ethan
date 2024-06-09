package com.ethan.serialize.fastjson2;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONWriter;
import com.ethan.serialize.ObjectOutput;

import java.io.IOException;
import java.io.OutputStream;

/**
 * FastJson object output implementation.
 *
 * @author Huang Z.Y.
 */
public class FastJson2ObjectOutput implements ObjectOutput {

    private final Fastjson2CreatorManager fastjson2CreatorManager;

    private volatile ClassLoader classLoader;
    private final OutputStream os;

    public FastJson2ObjectOutput(Fastjson2CreatorManager fastjson2CreatorManager, OutputStream out) {
        this.fastjson2CreatorManager = fastjson2CreatorManager;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.os = out;
        fastjson2CreatorManager.setCreator(classLoader);
    }

    @Override
    public void writeBool(boolean v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeByte(byte v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeShort(short v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeInt(int v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeLong(long v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeFloat(float v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeDouble(double v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeUTF(String v) throws IOException {
        writeObject(v);
    }

    @Override
    public void writeBytes(byte[] b) throws IOException {
        writeLength(b.length);
        os.write(b);
    }

    @Override
    public void writeBytes(byte[] b, int off, int len) throws IOException {
        writeLength(len);
        os.write(b, off, len);
    }

    @Override
    public void writeObject(Object obj) throws IOException {
        updateClassLoaderIfNeed();
        byte[] bytes = JSONB.toBytes(
                obj,
                JSONWriter.Feature.WriteClassName,
                JSONWriter.Feature.FieldBased,
                JSONWriter.Feature.ErrorOnNoneSerializable,
                JSONWriter.Feature.ReferenceDetection,
                JSONWriter.Feature.WriteNulls,
                JSONWriter.Feature.NotWriteDefaultValue,
                JSONWriter.Feature.NotWriteHashMapArrayListClassName,
                JSONWriter.Feature.WriteNameAsSymbol);
        writeLength(bytes.length);
        os.write(bytes);
        os.flush();
    }

    private void updateClassLoaderIfNeed() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        if (currentClassLoader != classLoader) {
            fastjson2CreatorManager.setCreator(currentClassLoader);
            classLoader = currentClassLoader;
        }
    }

    private void writeLength(int value) throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        int length = bytes.length;
        for (int i = 0; i < length; i++) {
            bytes[length - i - 1] = (byte) (value & 0xFF);
            value >>= 8;
        }
        os.write(bytes);
    }

    @Override
    public void flushBuffer() throws IOException {
        os.flush();
    }

}

