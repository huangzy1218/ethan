package com.ethan.common.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author Huang Z.Y.
 */
@AllArgsConstructor
@Getter
public enum SerializationType {

    FASTJSON2((byte) 1, "fastjson2"),
    JDK((byte) 2, "jdk"),
    HESSIAN2((byte) 3, "hessian2"),
    KRYO((byte) 4, "kryo");

    private final byte code;
    private final String name;

    /**
     * Get the name corresponding to the given code.
     *
     * @param code The code of the serialization type
     * @return The name of the serialization type, or null if not found
     */
    public static String getName(byte code) {
        for (SerializationType type : SerializationType.values()) {
            if (type.getCode() == code) {
                return type.getName();
            }
        }
        return "fastjson2";
    }

    /**
     * Get the code corresponding to the given name.
     *
     * @param name The name of the serialization type
     * @return The code of the serialization type, or null if not found
     */
    public static Byte getCode(String name) {
        for (SerializationType type : SerializationType.values()) {
            if (type.getName().equalsIgnoreCase(name)) {
                return type.getCode();
            }
        }
        return 1;
    }

}
