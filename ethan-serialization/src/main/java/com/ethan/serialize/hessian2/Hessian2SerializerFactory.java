package com.ethan.serialize.hessian2;

import com.alibaba.com.caucho.hessian.io.*;
import com.ethan.common.extension.SPI;

import java.io.Serializable;

/**
 * Create serializer and deserializer.
 *
 * @author Huang Z.Y.
 */
@SPI("hessian2")
public class Hessian2SerializerFactory extends SerializerFactory {

    public Hessian2SerializerFactory(
            ClassLoader classLoader) {
        super(classLoader);
    }

    @Override
    public Class<?> loadSerializedClass(String className) throws ClassNotFoundException {
        return null;
    }

    @Override
    protected Serializer getDefaultSerializer(Class cl) {
        if (_defaultSerializer != null) {
            return _defaultSerializer;
        }

        checkSerializable(cl);

        return new JavaSerializer(cl, getClassLoader());
    }

    @Override
    protected Deserializer getDefaultDeserializer(Class cl) {
        checkSerializable(cl);

        return new JavaDeserializer(cl);
    }

    private void checkSerializable(Class<?> cl) {
        // If class is Serializable => ok
        // If class has not implement Serializable
        // If hessian check serializable => fail
        // If ethan class checker check serializable => fail
        // If both hessian and dubbo class checker allow non-serializable => ok
        if (!Serializable.class.isAssignableFrom(cl)
                && (!isAllowNonSerializable())) {
            throw new IllegalStateException(
                    "Serialized class " + cl.getName() + " must implement java.io.Serializable");
        }
    }

}
