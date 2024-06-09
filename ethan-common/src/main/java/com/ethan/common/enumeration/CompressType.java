package com.ethan.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Huang Z.Y.
 */
@Getter
@AllArgsConstructor
public enum CompressType {

    IDENTITY((byte) 0, "identity"),
    GZIP((byte) 0x01, "gzip"),
    BZIP2((byte) 0x10, "bzip2"),
    SNAPPY((byte) 0x11, "snappy");

    private final byte code;
    private final String name;

    public static String getName(byte code) {
        for (CompressType c : CompressType.values()) {
            if (c.getCode() == code) {
                return c.name;
            }
        }
        return "gzip";
    }

    public static Byte getCode(String name) {
        for (CompressType c : CompressType.values()) {
            if (c.getName().equalsIgnoreCase(name)) {
                return c.getCode();
            }
        }
        return 1;
    }
}
