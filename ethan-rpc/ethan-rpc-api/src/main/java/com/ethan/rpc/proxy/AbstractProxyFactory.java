package com.ethan.rpc.proxy;

import com.ethan.common.util.ClassUtils;
import com.ethan.common.util.ReflectUtils;
import com.ethan.common.util.StringUtils;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.RpcException;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;

import static com.ethan.common.constant.CommonConstants.*;


/**
 * Abstract proxy factory implements {@link com.ethan.rpc.ProxyFactory}.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws Exception {
        return getProxy(invoker, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws Exception {
        // When compiling with native image, ensure that the order of the interfaces remains unchanged
        LinkedHashSet<Class<?>> interfaces = new LinkedHashSet<>();
        ClassLoader classLoader = getClassLoader(invoker);

        String config = invoker.getUrl().getParameter(INTERFACES);
        if (StringUtils.isNotEmpty(config)) {
            String[] types = COMMA_SPLIT_PATTERN.split(config);
            for (String type : types) {
                try {
                    interfaces.add(ReflectUtils.forName(classLoader, type));
                } catch (Throwable ignored) {
                }
            }
        }

        Class<?> realInterfaceClass = null;
        if (generic) {
            try {
                // Find the real interface from url
                String realInterface = invoker.getUrl().getParameter(INTERFACE);
                realInterfaceClass = ReflectUtils.forName(classLoader, realInterface);
                interfaces.add(realInterfaceClass);
            } catch (Throwable ignored) {
            }
        }

        interfaces.add(invoker.getInterface());

        try {
            return getProxy(invoker, interfaces.toArray(new Class<?>[0]));
        } catch (Throwable t) {
            if (generic) {
                if (realInterfaceClass != null) {
                    interfaces.remove(realInterfaceClass);
                }
                interfaces.remove(invoker.getInterface());

                log.error("Error occur when creating proxy. Invoker is in generic mode. " +
                        "Trying to create proxy without real interface class.", t);
                return getProxy(invoker, interfaces.toArray(new Class<?>[0]));
            } else {
                throw new RpcException(t);
            }
        }
    }

    private <T> ClassLoader getClassLoader(Invoker<T> invoker) {
        return ClassUtils.getClassLoader();
    }

    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types) throws Exception;

}
