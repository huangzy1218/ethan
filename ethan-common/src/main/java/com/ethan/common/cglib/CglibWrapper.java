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

    private final Class<?> wrapperClass;
    private final Class<?> targetClass;

    private CglibWrapper(Class<?> targetClass, Class<?> wrapperClass) {
        this.targetClass = targetClass;
        this.wrapperClass = wrapperClass;
    }

    public static CglibWrapper getWrapper(Class<?> clazz) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setSuperclass(clazz);
        enhancer.setCallback(new MethodInterceptorImpl());
        Class<?> proxyClass = enhancer.createClass();
        return new CglibWrapper(clazz, proxyClass);
    }

    public Object invokeMethod(Object instance, String methodName, Class<?>[] parameterTypes, Object[] args) throws Throwable {
        Method method = wrapperClass.getMethod(methodName, parameterTypes);
        return method.invoke(instance, args);
    }

    private static class MethodInterceptorImpl implements MethodInterceptor {
        @Override
        public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
            return proxy.invokeSuper(obj, args);
        }
    }

}
