package com.ethan.common.cglib;

import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodInterceptor;
import org.springframework.cglib.proxy.MethodProxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * CGLib proxy references {@link java.lang.reflect.Proxy}.
 *
 * @author Huang Z.Y.
 */
public class CglibProxy {

    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h) throws Exception {
        Enhancer enhancer = new Enhancer();
        enhancer.setClassLoader(loader);
        // Use the first interface as the parent class
        enhancer.setSuperclass(interfaces[0]);
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                // Call input InvocationHandler invoke method
                return h.invoke(obj, method, args);
            }
        });

        return enhancer.create();
    }

}
