package com.ethan.common.util;

/**
 * Class Tools.
 */
public class ClassUtils {

    public static boolean isMatch(Class<?> from, Class<?> to) {
        if (from == to) {
            return true;
        }
        boolean isMatch;
        // Primitive types and wrapper classes
        if (from.isPrimitive()) {
            isMatch = matchPrimitive(from, to);
        } else if (to.isPrimitive()) {
            isMatch = matchPrimitive(to, from);
        } else {
            isMatch = to.isAssignableFrom(from);
        }
        return isMatch;
    }

    private static boolean matchPrimitive(Class<?> from, Class<?> to) {
        if (from == boolean.class) {
            return to == Boolean.class;
        } else if (from == byte.class) {
            return to == Byte.class;
        } else if (from == char.class) {
            return to == Character.class;
        } else if (from == short.class) {
            return to == Short.class;
        } else if (from == int.class) {
            return to == Integer.class;
        } else if (from == long.class) {
            return to == Long.class;
        } else if (from == float.class) {
            return to == Float.class;
        } else if (from == double.class) {
            return to == Double.class;
        } else if (from == void.class) {
            return to == Void.class;
        }
        return false;
    }

}
