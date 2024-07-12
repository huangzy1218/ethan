package com.ethan.common.util;

/**
 * Byte utility for codec.
 *
 * @author Huang Z.Y.
 */
public class ByteUtils {

    /**
     * Byte array copy.
     *
     * @param src    Source
     * @param length New length.
     * @return New byte array.
     */
    public static byte[] copyOf(byte[] src, int length) {
        byte[] dest = new byte[length];
        System.arraycopy(src, 0, dest, 0, Math.min(src.length, length));
        return dest;
    }

    /**
     * Byte to int.
     *
     * @param b   Byte array
     * @param off Offset
     * @return Integer
     */
    public static int bytes2int(byte[] b, int off) {
        return ((b[off + 3] & 0xFF) << 0)
                + ((b[off + 2] & 0xFF) << 8)
                + ((b[off + 1] & 0xFF) << 16)
                + ((b[off + 0]) << 24);
    }

    /**
     * Short to byte array.
     *
     * @param v Value
     * @return Bytes array
     */
    public static byte[] short2bytes(short v) {
        byte[] ret = {0, 0};
        short2bytes(v, ret);
        return ret;
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b) {
        short2bytes(v, b, 0);
    }

    /**
     * to byte array.
     *
     * @param v value.
     * @param b byte array.
     */
    public static void short2bytes(short v, byte[] b, int off) {
        b[off + 1] = (byte) v;
        b[off + 0] = (byte) (v >>> 8);
    }

}
