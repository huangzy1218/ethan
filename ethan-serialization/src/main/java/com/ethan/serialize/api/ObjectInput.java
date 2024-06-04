package com.ethan.serialize.api;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Object input interface.
 *
 * @author Huang Z.Y.
 */
public interface ObjectInput extends DataInput {

    /**
     * Consider use {@link #readObject(Class)} or {@link #readObject(Class, java.lang.reflect.Type)} where possible.
     *
     * @return Object
     * @throws IOException            If an I/O error occurs
     * @throws ClassNotFoundException if an ClassNotFoundException occurs
     */
    @Deprecated
    Object readObject() throws IOException, ClassNotFoundException;

    /**
     * Read object.
     *
     * @param cls Object class
     * @return Object
     * @throws IOException            If an I/O error occurs
     * @throws ClassNotFoundException If an ClassNotFoundException occurs
     */
    <T> T readObject(Class<T> cls) throws IOException, ClassNotFoundException;

    /**
     * Read object.
     *
     * @param cls  Object class
     * @param type Object type
     * @return Object
     * @throws IOException            If an I/O error occurs
     * @throws ClassNotFoundException If an ClassNotFoundException occurs
     */
    <T> T readObject(Class<T> cls, Type type) throws IOException, ClassNotFoundException;

}