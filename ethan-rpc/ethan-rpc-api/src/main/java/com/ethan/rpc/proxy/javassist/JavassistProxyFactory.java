package com.ethan.rpc.proxy.javassist;

import com.ethan.common.URL;
import com.ethan.common.javassist.JavassistProxy;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.proxy.AbstractProxyFactory;
import com.ethan.rpc.proxy.AbstractProxyInvoker;
import com.ethan.rpc.proxy.InvokerInvocationHandler;
import com.ethan.rpc.proxy.jdk.JdkProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * Javassist implements {@link com.ethan.rpc.ProxyFactory}.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class JavassistProxyFactory extends AbstractProxyFactory {

    private final JdkProxyFactory jdkProxyFactory = new JdkProxyFactory();

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return new AbstractProxyInvoker<T>(proxy, type, url) {
            @Override
            protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
                    throws Throwable {
                return null;
            }
        };
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) throws Exception {
        try {
            return (T) JavassistProxy.newProxyInstance(
                    invoker.getInterface().getClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
        } catch (Throwable fromJavassist) {
            try {
                // Try fall back to JDK proxy factory
                T proxy = jdkProxyFactory.getProxy(invoker, interfaces);
                log.error("Failed to generate proxy by Javassist failed.", fromJavassist);
                return proxy;
            } catch (Exception fromJdk) {
                log.error("Failed to generate proxy by JDK failed.", fromJdk);
                throw fromJavassist;
            }
        }
    }

}
    