package com.ethan.serialize.fastjson2;

import com.alibaba.fastjson2.JSONB;
import com.alibaba.fastjson2.JSONReader;
import com.ethan.common.util.ClassUtils;
import com.ethan.serialize.api.ObjectInput;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * FastJson object input implementation.
 *
 * @author Huang Z.Y
 */
public class FastJson2ObjectInput implements ObjectInput {

    private final Fastjson2CreatorManager fastjson2CreatorManager;

    private volatile ClassLoader classLoader;
    private final InputStream is;

    public FastJson2ObjectInput(Fastjson2CreatorManager fastjson2CreatorManager, InputStream in) {
        this.fastjson2CreatorManager = fastjson2CreatorManager;
        this.classLoader = Thread.currentThread().getContextClassLoader();
        this.is = in;
        fastjson2CreatorManager.setCreator(classLoader);
    }

    @Override
    public boolean readBool() throws IOException {
        return readObject(boolean.class);
    }

    @Override
    public byte readByte() throws IOException {
        return readObject(byte.class);
    }

    @Override
    public short readShort() throws IOException {
        return readObject(short.class);
    }

    @Override
    public int readInt() throws IOException {
        return readObject(int.class);
    }

    @Override
    public long readLong() throws IOException {
        return readObject(long.class);
    }

    @Override
    public float readFloat() throws IOException {
        return readObject(float.class);
    }

    @Override
    public double readDouble() throws IOException {
        return readObject(double.class);
    }

    @Override
    public String readUTF() throws IOException {
        return readObject(String.class);
    }

    @Override
    public byte[] readBytes() throws IOException {
        int length = readLength();
        byte[] bytes = new byte[length];
        int read = is.read(bytes, 0, length);
        if (read != length) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected read length: " + length + " but actual read: " + read);
        }
        return bytes;
    }

    @Override
    public Object readObject() throws IOException, ClassNotFoundException {
        return readObject(Object.class);
    }

    @Override
    public <T> T readObject(Class<T> cls) throws IOException {
        updateClassLoaderIfNeed();
        int length = readLength();
        byte[] bytes = new byte[length];
        int read = is.read(bytes, 0, length);
        if (read != length) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected read length: " + length + " but actual read: " + read);
        }
        T result = JSONB.parseObject(
                bytes,
                cls,
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.ErrorOnNoneSerializable,
                JSONReader.Feature.IgnoreAutoTypeNotMatch,
                JSONReader.Feature.UseNativeObject,
                JSONReader.Feature.FieldBased);
        if (result != null && cls != null && !ClassUtils.isMatch(result.getClass(), cls)) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected class: " + cls + " but actual class: " + result.getClass());
        }
        return result;
    }

    @Override
    public <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException {
        updateClassLoaderIfNeed();
        int length = readLength();
        byte[] bytes = new byte[length];
        int read = is.read(bytes, 0, length);
        if (read != length) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected read length: " + length + " but actual read: " + read);
        }
        T result = JSONB.parseObject(
                bytes,
                cls,
                JSONReader.Feature.UseDefaultConstructorAsPossible,
                JSONReader.Feature.ErrorOnNoneSerializable,
                JSONReader.Feature.IgnoreAutoTypeNotMatch,
                JSONReader.Feature.UseNativeObject);

        if (result != null && cls != null && !ClassUtils.isMatch(result.getClass(), cls)) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected class: " + cls + " but actual class: " + result.getClass());
        }
        return result;
    }

    private void updateClassLoaderIfNeed() {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        if (currentClassLoader != classLoader) {
            fastjson2CreatorManager.setCreator(currentClassLoader);
            classLoader = currentClassLoader;
        }
    }

    private int readLength() throws IOException {
        byte[] bytes = new byte[Integer.BYTES];
        int read = is.read(bytes, 0, Integer.BYTES);
        if (read != Integer.BYTES) {
            throw new IllegalArgumentException(
                    "deserialize failed. expected read length: " + Integer.BYTES + " but actual read: " + read);
        }
        int value = 0;
        for (byte b : bytes) {
            value = (value << 8) + (b & 0xFF);
        }
        return value;
    }

}

