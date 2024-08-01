package com.ethan.rpc;

import com.ethan.common.extension.SPI;

/**
 * RPC Protocol extension interface, which encapsulates the details of remote invocation.
 *
 * @author Huang Z.Y.
 */
@SPI("remote")
public interface Protocol {

    /**
     * Export service for remote invocation.
     *
     * @param <T>     Service type
     * @param invoker Service invoker
     * @return Exporter reference for exported service, useful for unexport the service later
     * @throws RpcException Thrown when error occurs during export the service, for example: port is occupied
     */
    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

    /**
     * Refer a remote service.
     *
     * @param <T>  Service type
     * @param type Service class
     * @param url  URL address for the remote service
     * @return Invoker service's local proxy
     * @throws RpcException When there's any error while connecting to the service provider
     */
    <T> Invoker<T> refer(Class<T> type) throws RpcException;

}
