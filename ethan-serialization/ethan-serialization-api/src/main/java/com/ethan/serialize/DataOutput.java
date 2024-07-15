package com.ethan.serialize;

import java.io.IOException;

/**
 * Basic data type output interface.
 *
 * @author Huang Z.Y.
 */
public interface DataOutput {

    /**
     * Write boolean.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeBool(boolean v) throws IOException;

    /**
     * Write byte.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeByte(byte v) throws IOException;

    /**
     * Write short.
     *
     * @param v Value
     * @throws IOException
     */
    void writeShort(short v) throws IOException;

    /**
     * Write integer.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeInt(int v) throws IOException;

    /**
     * Write long.
     *
     * @param v Value
     * @throws IOException
     */
    void writeLong(long v) throws IOException;

    /**
     * Write float.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeFloat(float v) throws IOException;

    /**
     * Write double.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeDouble(double v) throws IOException;

    /**
     * Write string.
     *
     * @param v Value
     * @throws IOException If an I/O error occurs
     */
    void writeUTF(String v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v Value
     * @throws IOException
     */
    void writeBytes(byte[] v) throws IOException;

    /**
     * Write byte array.
     *
     * @param v   Value.
     * @param off The start offset in the data
     * @param len The number of bytes that are written
     * @throws IOException If an I/O error occurs
     */
    void writeBytes(byte[] v, int off, int len) throws IOException;

    /**
     * Flush buffer.
     *
     * @throws IOException If an I/O error occurs
     */
    void flushBuffer() throws IOException;

}