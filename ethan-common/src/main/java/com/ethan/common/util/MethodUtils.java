package com.ethan.common.util;

import java.lang.reflect.Method;

/**
 * Method tools.
 *
 * @author Huang Z.Y.
 */
public class MethodUtils {

    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    /**
     * Invoke the target object and method.
     *
     * @param object           The target object
     * @param methodName       The method name
     * @param methodParameters The method parameters
     * @param <T>              The return type
     * @return The target method's execution result
     */
    static <T> T invokeMethod(Object object, String methodName, Object... methodParameters) {
        Class type = object.getClass();
        Class[] parameterTypes = resolveTypes(methodParameters);
        Method method = findMethod(type, methodName, parameterTypes);
        T value = null;

        if (method == null) {
            throw new IllegalStateException(
                    String.format("cannot find method %s,class: %s", methodName, type.getName()));
        }

        try {
            final boolean isAccessible = method.isAccessible();

            if (!isAccessible) {
                method.setAccessible(true);
            }
            value = (T) method.invoke(object, methodParameters);
            method.setAccessible(isAccessible);
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }

        return value;
    }

    static Method findMethod(Class type, String methodName, Class<?>... parameterTypes) {
        Method method = null;
        try {
            if (type != null && StringUtils.isNotEmpty(methodName)) {
                method = type.getDeclaredMethod(methodName, parameterTypes);
            }
        } catch (NoSuchMethodException e) {
        }
        return method;
    }

    /**
     * Resolve the types of the specified values
     *
     * @param values the values
     * @return If can't be resolved, return {@link MethodUtils#EMPTY_CLASS_ARRAY} empty class array}
     * @since 2.7.6
     */
    public static Class[] resolveTypes(Object... values) {
        if (StringUtils.isEmpty(values)) {
            return EMPTY_CLASS_ARRAY;
        }

        int size = values.length;

        Class[] types = new Class[size];

        for (int i = 0; i < size; i++) {
            Object value = values[i];
            types[i] = value == null ? null : value.getClass();
        }

        return types;
    }

}
