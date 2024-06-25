package com.ethan.rpc.descriptor;

import com.ethan.common.util.ReflectUtils;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.stream.Stream;

/**
 * @author Huang Z.Y.
 */
@Slf4j
public class ReflectionMethodDescriptor implements MethodDescriptor {

    public final String methodName;
    private final Class<?>[] parameterClasses;
    private final Class<?> returnClass;
    private final Type[] returnTypes;
    private final Method method;
    private final String[] compatibleParamSignatures;

    public ReflectionMethodDescriptor(Method method) {
        this.method = method;
        this.methodName = method.getName();
        this.parameterClasses = method.getParameterTypes();
        this.returnClass = method.getReturnType();
        Type[] returnTypesResult;
        try {
            returnTypesResult = ReflectUtils.getReturnTypes(method);
        } catch (Throwable throwable) {
            log.error("Fail to get return types. Method name: " + methodName + " Declaring class:"
                            + method.getDeclaringClass().getName(),
                    throwable);
            returnTypesResult = new Type[]{returnClass, returnClass};
        }
        this.returnTypes = returnTypesResult;
        this.compatibleParamSignatures =
                Stream.of(parameterClasses).map(Class::getName).toArray(String[]::new);
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String[] getCompatibleParamSignatures() {
        return compatibleParamSignatures;
    }

    @Override
    public Class<?>[] getParameterClasses() {
        return parameterClasses;
    }

    @Override
    public Class<?> getReturnClass() {
        return returnClass;
    }

    @Override
    public Type[] getReturnTypes() {
        return returnTypes;
    }

    @Override
    public Method getMethod() {
        return method;
    }

}
    