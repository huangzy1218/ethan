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

    public static ClassLoader getClassLoader() {
        return getClassLoader(ClassUtils.class);
    }

    /**
     * Get class loader.
     *
     * @param clazz Class type
     * @return Class loader
     */
    public static ClassLoader getClassLoader(Class<?> clazz) {
        ClassLoader cl = null;
        try {
            cl = Thread.currentThread().getContextClassLoader();
        } catch (Exception ignored) {
            // Cannot access thread context ClassLoader - falling back to system class loader...
        }
        if (cl == null) {
            // No thread context class loader -> use class loader of this class.
            cl = clazz.getClassLoader();
            if (cl == null) {
                // getClassLoader() returning null indicates the bootstrap ClassLoader
                try {
                    cl = ClassLoader.getSystemClassLoader();
                } catch (Exception ignored) {
                    // Cannot access system ClassLoader - oh well, maybe the caller can live with null...
                }
            }
        }

        return cl;
    }

}
