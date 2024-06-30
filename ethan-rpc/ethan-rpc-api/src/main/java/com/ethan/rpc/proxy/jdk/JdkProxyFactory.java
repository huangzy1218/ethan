package com.ethan.rpc.proxy.jdk;

import com.ethan.common.URL;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.proxy.AbstractProxyFactory;
import com.ethan.rpc.proxy.AbstractProxyInvoker;
import com.ethan.rpc.proxy.InvokerInvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * JDK implements {@link com.ethan.rpc.ProxyFactory}.
 *
 * @author Huang Z.Y.
 */
public class JdkProxyFactory extends AbstractProxyFactory {

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
                    throws Throwable {
                Method method = proxy.getClass().getMethod(methodName, parameterTypes);
                return method.invoke(proxy, arguments);
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) {
        return (T) Proxy.newProxyInstance(
                invoker.getInterface().getClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
    }

}
    