package com.ethan.rpc;

import com.ethan.common.URL;

/**
 * Proxy factory.
 *
 * @author Huang Z.Y.
 */
public interface ProxyFactory {

    /**
     * Create proxy.
     *
     * @param invoker A executable service calls
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    /**
     * Create proxy.
     *
     * @param invoker A executable service calls
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker, boolean generic) throws RpcException;

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
