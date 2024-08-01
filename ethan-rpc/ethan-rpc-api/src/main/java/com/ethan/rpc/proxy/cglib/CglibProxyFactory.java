package com.ethan.rpc.proxy.cglib;

import com.ethan.common.URL;
import com.ethan.common.cglib.CglibProxy;
import com.ethan.common.cglib.CglibWrapper;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.proxy.AbstractProxyFactory;
import com.ethan.rpc.proxy.AbstractProxyInvoker;
import com.ethan.rpc.proxy.InvokerInvocationHandler;
import com.ethan.rpc.proxy.jdk.JdkProxyFactory;
import lombok.extern.slf4j.Slf4j;

/**
 * CglibProxyFactory implements {@link com.ethan.rpc.ProxyFactory}.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class CglibProxyFactory extends AbstractProxyFactory {

    private final JdkProxyFactory jdkProxyFactory = new JdkProxyFactory();

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getProxy(Invoker<T> invoker, Class<?>[] interfaces) throws Exception {
        try {
            return (T) CglibProxy.newProxyInstance(
                    invoker.getInterface().getClassLoader(), interfaces, new InvokerInvocationHandler(invoker));
        } catch (Exception fromJavassist) {
            try {
                // Try fall back to JDK proxy factory
                T proxy = jdkProxyFactory.getProxy(invoker, interfaces);
                log.error("Failed to generate proxy by CGLib.", fromJavassist);
                return proxy;
            } catch (Exception fromJdk) {
                log.error("Failed to generate proxy by JDK.", fromJdk);
                throw fromJavassist;
            }
        }
    }

    @Override
    public <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException {
        return new AbstractProxyInvoker<T>(proxy, type) {
            @Override
            protected Object doInvoke(T proxy, String methodName, Class<?>[] parameterTypes, Object[] arguments)
                    throws Throwable {
                CglibWrapper wrapper = CglibWrapper.getWrapper(proxy.getClass());
                return wrapper.invokeMethod(methodName, parameterTypes, arguments);
            }
        };
    }

}
