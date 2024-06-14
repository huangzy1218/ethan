package com.ethan.rpc.proxy;

import com.ethan.common.util.ReflectUtils;
import com.ethan.common.util.StringUtils;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.model.ServiceModel;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedHashSet;

import static com.ethan.common.constant.CommonConstants.COMMA_SPLIT_PATTERN;
import static com.ethan.rpc.Constants.INTERFACE;
import static com.ethan.rpc.Constants.INTERFACES;


/**
 * Abstract proxy factory implements {@link com.ethan.rpc.ProxyFactory}.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public abstract class AbstractProxyFactory implements ProxyFactory {

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        return getProxy(invoker, false);
    }

    @Override
    public <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException {
        // when compiling with native image, ensure that the order of the interfaces remains unchanged
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
                // find the real interface from url
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

                logger.error(
                        PROXY_UNSUPPORTED_INVOKER,
                        "",
                        "",
                        "Error occur when creating proxy. Invoker is in generic mode. Trying to create proxy without real interface class.",
                        t);
                return getProxy(invoker, interfaces.toArray(new Class<?>[0]));
            } else {
                throw t;
            }
        }
    }

    private <T> ClassLoader getClassLoader(Invoker<T> invoker) {
        ServiceModel serviceModel = invoker.getUrl().getServiceModel();
        ClassLoader classLoader = null;
        if (serviceModel != null && serviceModel.getInterfaceClassLoader() != null) {
            classLoader = serviceModel.getInterfaceClassLoader();
        }
        if (classLoader == null) {
            classLoader = ClassUtils.getClassLoader();
        }
        return classLoader;
    }

    public static Class<?>[] getInternalInterfaces() {
        return INTERNAL_INTERFACES.clone();
    }

    public abstract <T> T getProxy(Invoker<T> invoker, Class<?>[] types);

}
