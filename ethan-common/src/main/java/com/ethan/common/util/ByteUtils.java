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

    /**
     * To long.
     *
     * @param b Byte array
     * @return Long
     */
    public static long bytes2long(byte[] b) {
        return bytes2long(b, 0);
    }

    /**
     * To long
     *
     * @param b   Byte array
     * @param off Offset
     * @return Long
     */
    public static long bytes2long(byte[] b, int off) {
        return ((b[off + 7] & 0xFFL) << 0)
                + ((b[off + 6] & 0xFFL) << 8)
                + ((b[off + 5] & 0xFFL) << 16)
                + ((b[off + 4] & 0xFFL) << 24)
                + ((b[off + 3] & 0xFFL) << 32)
                + ((b[off + 2] & 0xFFL) << 40)
                + ((b[off + 1] & 0xFFL) << 48)
                + (((long) b[off + 0]) << 56);
    }

    /**
     * To byte array
     *
     * @param v   Value
     * @param b   Byte array
     * @param off Array offset
     */
    public static void long2bytes(long v, byte[] b, int off) {
        b[off + 7] = (byte) v;
        b[off + 6] = (byte) (v >>> 8);
        b[off + 5] = (byte) (v >>> 16);
        b[off + 4] = (byte) (v >>> 24);
        b[off + 3] = (byte) (v >>> 32);
        b[off + 2] = (byte) (v >>> 40);
        b[off + 1] = (byte) (v >>> 48);
        b[off + 0] = (byte) (v >>> 56);
    }

    /**
     * To byte array.
     *
     * @param v Value
     * @return Byte array
     */
    public static byte[] int2bytes(int v) {
        byte[] ret = {0, 0, 0, 0};
        int2bytes(v, ret);
        return ret;
    }

    /**
     * To byte array.
     *
     * @param v Value
     * @param b Byte array
     */
    public static void int2bytes(int v, byte[] b) {
        int2bytes(v, b, 0);
    }

    /**
     * To byte array.
     *
     * @param v   Value
     * @param b   Byte array
     * @param off Array offset
     */
    public static void int2bytes(int v, byte[] b, int off) {
        b[off + 3] = (byte) v;
        b[off + 2] = (byte) (v >>> 8);
        b[off + 1] = (byte) (v >>> 16);
        b[off + 0] = (byte) (v >>> 24);
    }


}
