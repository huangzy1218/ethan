package com.ethan.common.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * CGLib wrapper.
 *
 * @author Huang Z.Y.
 */
public class CglibWrapper {

    private final Class<?> targetClass;
    private final Object proxyInstance;

    private CglibWrapper(Class<?> targetClass, Object proxyInstance) {
        this.targetClass = targetClass;
        this.proxyInstance = proxyInstance;
    }

    public static CglibWrapper getWrapper(Class<?> clazz) {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                return proxy.invokeSuper(obj, args);
            }
        });
        Object proxyInstance = enhancer.create();
        return new CglibWrapper(clazz, proxyInstance);
    }

    public Object invokeMethod(String methodName, Class<?>[] parameterTypes, Object[] args) throws Throwable {
        Method method = targetClass.getMethod(methodName, parameterTypes);
        return method.invoke(proxyInstance, args);
    }

}
