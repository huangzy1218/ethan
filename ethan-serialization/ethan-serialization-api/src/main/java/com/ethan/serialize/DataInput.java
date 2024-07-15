package com.ethan.serialize;

import java.io.IOException;

/**
 * Basic data type input interface.
 *
 * @author Huang Z.Y.
 */
public interface DataInput {

    /**
     * Read boolean.
     *
     * @return Boolean
     * @throws IOException If an I/O error occurs
     */
    boolean readBool() throws IOException;

    /**
     * Read byte.
     *
     * @return Byte value
     * @throws IOException If an I/O error occurs
     */
    byte readByte() throws IOException;

    /**
     * Read short integer
     *
     * @return Short
     * @throws IOException If an I/O error occurs
     */
    short readShort() throws IOException;

    /**
     * Read integer.
     *
     * @return Integer
     * @throws IOException If an I/O error occurs
     */
    int readInt() throws IOException;

    /**
     * Read long.
     *
     * @return Long
     * @throws IOException If an I/O error occurs
     */
    long readLong() throws IOException;

    /**
     * Read float.
     *
     * @return Float
     * @throws IOException If an I/O error occurs
     */
    float readFloat() throws IOException;

    /**
     * Read double.
     *
     * @return Double
     * @throws IOException If an I/O error occurs
     */
    double readDouble() throws IOException;

    /**
     * Read UTF-8 string.
     *
     * @return String
     * @throws IOException If an I/O error occurs
     */
    String readUTF() throws IOException;

    /**
     * Read byte array.
     *
     * @return Byte array
     * @throws IOException If an I/O error occurs
     */
    byte[] readBytes() throws IOException;

}
