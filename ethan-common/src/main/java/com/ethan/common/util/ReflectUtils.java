package com.ethan.common.util;

import javassist.NotFoundException;

import java.lang.reflect.*;
import java.util.concurrent.Future;

/**
 * Reflection utility.
 *
 * @author Huang Z.Y.
 */
public class ReflectUtils {

    /**
     * void(V).
     */
    public static final char JVM_VOID = 'V';

    /**
     * boolean(Z).
     */
    public static final char JVM_BOOLEAN = 'Z';

    /**
     * byte(B).
     */
    public static final char JVM_BYTE = 'B';

    /**
     * char(C).
     */
    public static final char JVM_CHAR = 'C';

    /**
     * double(D).
     */
    public static final char JVM_DOUBLE = 'D';

    /**
     * float(F).
     */
    public static final char JVM_FLOAT = 'F';

    /**
     * int(I).
     */
    public static final char JVM_INT = 'I';

    /**
     * long(J).
     */
    public static final char JVM_LONG = 'J';

    /**
     * short(S).
     */
    public static final char JVM_SHORT = 'S';

    private static ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    public static Class<?> forName(ClassLoader cl, String name) {
        try {
            return name2class(cl, name);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Not found class " + name + ", cause: " + e.getMessage(), e);
        }
    }

    /**
     * Name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     *
     * @param name Name
     * @return Class instance
     */
    public static Class<?> name2class(String name) throws ClassNotFoundException {
        return name2class(classLoader, name);
    }

    /**
     * Name to class.
     * "boolean" => boolean.class
     * "java.util.Map[][]" => java.util.Map[][].class
     *
     * @param cl   ClassLoader instance
     * @param name Name
     * @return Class instance
     */
    private static Class<?> name2class(ClassLoader cl, String name) throws ClassNotFoundException {
        int c = 0, index = name.indexOf('[');
        if (index > 0) {
            c = (name.length() - index) / 2;
            name = name.substring(0, index);
        }
        if (c > 0) {
            StringBuilder sb = new StringBuilder();
            while (c-- > 0) {
                sb.append('[');
            }

            if ("void".equals(name)) {
                sb.append(JVM_VOID);
            } else if ("boolean".equals(name)) {
                sb.append(JVM_BOOLEAN);
            } else if ("byte".equals(name)) {
                sb.append(JVM_BYTE);
            } else if ("char".equals(name)) {
                sb.append(JVM_CHAR);
            } else if ("double".equals(name)) {
                sb.append(JVM_DOUBLE);
            } else if ("float".equals(name)) {
                sb.append(JVM_FLOAT);
            } else if ("int".equals(name)) {
                sb.append(JVM_INT);
            } else if ("long".equals(name)) {
                sb.append(JVM_LONG);
            } else if ("short".equals(name)) {
                sb.append(JVM_SHORT);
            } else {
                // "java.lang.Object" ==> "Ljava.lang.Object;"
                sb.append('L').append(name).append(';');
            }
            name = sb.toString();
        } else {
            if ("void".equals(name)) {
                return void.class;
            }
            if ("boolean".equals(name)) {
                return boolean.class;
            }
            if ("byte".equals(name)) {
                return byte.class;
            }
            if ("char".equals(name)) {
                return char.class;
            }
            if ("double".equals(name)) {
                return double.class;
            }
            if ("float".equals(name)) {
                return float.class;
            }
            if ("int".equals(name)) {
                return int.class;
            }
            if ("long".equals(name)) {
                return long.class;
            }
            if ("short".equals(name)) {
                return short.class;
            }
        }

        return Class.forName(name, true, cl);
    }

    public static Type[] getReturnTypes(Method method) {
        Class<?> returnType = method.getReturnType();
        Type genericReturnType = method.getGenericReturnType();
        if (Future.class.isAssignableFrom(returnType)) {
            if (genericReturnType instanceof ParameterizedType) {
                Type actualArgType = ((ParameterizedType) genericReturnType).getActualTypeArguments()[0];
                if (actualArgType instanceof ParameterizedType) {
                    returnType = (Class<?>) ((ParameterizedType) actualArgType).getRawType();
                    genericReturnType = actualArgType;
                } else if (actualArgType instanceof TypeVariable) {
                    returnType = (Class<?>) ((TypeVariable<?>) actualArgType).getBounds()[0];
                    genericReturnType = actualArgType;
                } else {
                    returnType = (Class<?>) actualArgType;
                    genericReturnType = returnType;
                }
            } else {
                returnType = null;
                genericReturnType = null;
            }
        }
        return new Type[]{returnType, genericReturnType};
    }

    public static String getName(final Constructor<?> c) {
        StringBuilder ret = new StringBuilder("(");
        Class<?>[] parameterTypes = c.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    public static String getName(Class<?> c) {
        if (c.isArray()) {
            StringBuilder sb = new StringBuilder();
            do {
                sb.append("[]");
                c = c.getComponentType();
            } while (c.isArray());

            return c.getName() + sb.toString();
        }
        return c.getName();
    }

    public static String getName(final Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append(getName(m.getReturnType())).append(' ');
        ret.append(m.getName()).append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (i > 0) {
                ret.append(',');
            }
            ret.append(getName(parameterTypes[i]));
        }
        ret.append(')');
        return ret.toString();
    }

    /**
     * get method desc.
     * "(I)I", "()V", "(Ljava/lang/String;Z)V"
     *
     * @param m method.
     * @return desc.
     */
    public static String getDescWithoutMethodName(Method m) {
        StringBuilder ret = new StringBuilder();
        ret.append('(');
        Class<?>[] parameterTypes = m.getParameterTypes();
        for (int i = 0; i < parameterTypes.length; i++) {
            ret.append(getDesc(parameterTypes[i]));
        }
        ret.append(')').append(getDesc(m.getReturnType()));
        return ret.toString();
    }

    /**
     * Get class desc.
     * <ul>
     *     <li>boolean[].class => "[Z"</li>
     *     <li>Object.class => "Ljava/lang/Object;"</li>
     * </ul>
     *
     * @param c Class
     * @return Desc
     * @throws NotFoundException
     */
    public static String getDesc(Class<?> c) {
        StringBuilder ret = new StringBuilder();

        while (c.isArray()) {
            ret.append('[');
            c = c.getComponentType();
        }

        if (c.isPrimitive()) {
            String t = c.getName();
            if ("void".equals(t)) {
                ret.append(JVM_VOID);
            } else if ("boolean".equals(t)) {
                ret.append(JVM_BOOLEAN);
            } else if ("byte".equals(t)) {
                ret.append(JVM_BYTE);
            } else if ("char".equals(t)) {
                ret.append(JVM_CHAR);
            } else if ("double".equals(t)) {
                ret.append(JVM_DOUBLE);
            } else if ("float".equals(t)) {
                ret.append(JVM_FLOAT);
            } else if ("int".equals(t)) {
                ret.append(JVM_INT);
            } else if ("long".equals(t)) {
                ret.append(JVM_LONG);
            } else if ("short".equals(t)) {
                ret.append(JVM_SHORT);
            }
        } else {
            ret.append('L');
            ret.append(c.getName().replace('.', '/'));
            ret.append(';');
        }
        return ret.toString();
    }

}
