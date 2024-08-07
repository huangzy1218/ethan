package com.ethan.rpc;

import com.ethan.common.URL;
import com.ethan.common.extension.SPI;

/**
 * Proxy factory.
 *
 * @author Huang Z.Y.
 */
@SPI
public interface ProxyFactory {

    /**
     * Create proxy.
     *
     * @param invoker A executable service calls
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker) throws Exception;

    /**
     * Create proxy.
     *
     * @param invoker A executable service calls
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker, boolean generic) throws Exception;

    /**
     * Create invoker.
     *
     * @param proxy Proxy class
     * @param type  Invoker type
     * @return invoker
     */
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;

    default <T> Invoker<T> getInvoker(T proxy, Class<T> type) throws RpcException {
        return getInvoker(proxy, type, URL.buildFixedURL());
    }

    @SuppressWarnings("unchecked")
    default <T> Invoker<?> getInvoker(Object proxy, Class<?> type, URL url, int tag) throws RpcException {
        return getInvoker((T) proxy, (Class<? super T>) type, url);
    }

}
