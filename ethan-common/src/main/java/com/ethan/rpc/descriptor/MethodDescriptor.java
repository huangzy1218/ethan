package com.ethan.rpc.descriptor;

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * @author Huang Z.Y.
 */
public interface MethodDescriptor {

    String getMethodName();

    /**
     * duplicate filed as paramDesc, but with different format.
     */
    String[] getCompatibleParamSignatures();

    Class<?>[] getParameterClasses();

    Class<?> getReturnClass();

    Type[] getReturnTypes();

    /**
     * Only available for ReflectionMethod.
     *
     * @return Method
     */
    Method getMethod();

}
