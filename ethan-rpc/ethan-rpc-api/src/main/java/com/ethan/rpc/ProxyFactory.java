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
     * @param url   Invoker information
     * @return invoker
     */
    <T> Invoker<T> getInvoker(T proxy, Class<T> type, URL url) throws RpcException;

}
