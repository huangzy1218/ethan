package com.ethan.common.config;

import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.ConcurrentHashMapUtils;
import com.ethan.common.util.ReflectUtils;
import com.ethan.common.util.StringUtils;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Utility methods and public methods for parsing configuration.
 *
 * @author Huang Z.Y.
 */
public abstract class AbstractConfig implements Serializable {

    private static final long serialVersionUID = -3553540556580610367L;

    /**
     * Tag name cache, speed up get tag name frequently.
     */
    private static final ConcurrentMap<Class, String> tagNameCache = new ConcurrentHashMap<>();
    /**
     * The suffix container.
     */
    private static final String[] SUFFIXES = new String[]{"Config", "Bean", "ConfigBase"};
    /**
     * The config id.
     */
    @Getter
    @Setter
    private String id;

    public static String getTagName(Class<?> cls) {
        return ConcurrentHashMapUtils.computeIfAbsent(tagNameCache, cls, (key) -> {
            String tag = cls.getSimpleName();
            for (String suffix : SUFFIXES) {
                if (tag.endsWith(suffix)) {
                    tag = tag.substring(0, tag.length() - suffix.length());
                    break;
                }
            }
            return StringUtils.camelToSplitName(tag, "-");
        });
    }

    private static Map<String, String> invokeGetParameters(Class c, Object o) {
        try {

            Method method = ReflectUtils.findMethodByMethodSignature(c, "getParameters", null);
            if (method != null && isParametersGetter(method)) {
                return (Map<String, String>) method.invoke(o);
            }
        } catch (Throwable t) {
            // ignore
        }
        return null;
    }

    private static boolean isParametersGetter(Method method) {
        String name = method.getName();
        return ("getParameters".equals(name)
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterTypes().length == 0
                && method.getReturnType() == Map.class);
    }

    private static void invokeSetParameters(Class c, Object o, Map map) {
        try {
            Method method = ReflectUtils.findMethodByMethodSignature(c, "setParameters", new String[]{Map.class.getName()});
            if (method != null && isParametersSetter(method)) {
                method.invoke(o, map);
            }
        } catch (Throwable t) {
            // ignore
        }
    }

    private static boolean isParametersSetter(Method method) {
        return ("setParameters".equals(method.getName())
                && Modifier.isPublic(method.getModifiers())
                && method.getParameterCount() == 1
                && Map.class == method.getParameterTypes()[0]
                && method.getReturnType() == void.class);
    }

    public static void invokeSetter(Class<?> clazz, Object target, String propertyName, Object propertyValue) {
        try {
            // Construct the setter method name
            String setterMethodName = "set" + StringUtils.capitalize(propertyName);

            // Find the setter method
            Method setterMethod = clazz.getMethod(setterMethodName, propertyValue.getClass());
            // Invoke the setter to assign the value
            setterMethod.invoke(target, propertyValue);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            // ignore
        }
    }

    private void invokeSetParameters(Map<String, String> values, Object obj) {
        if (CollectionUtils.isEmptyMap(values)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        Map<String, String> getParametersMap = invokeGetParameters(obj.getClass(), obj);
        if (getParametersMap != null && !getParametersMap.isEmpty()) {
            map.putAll(getParametersMap);
        }
        map.putAll(values);
        invokeSetParameters(obj.getClass(), obj, map);
    }

}
