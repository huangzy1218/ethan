package com.ethan.rpc;

/**
 * RPC Protocol extension interface, which encapsulates the details of remote invocation.
 *
 * @author Huang Z.Y.
 */
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

}
