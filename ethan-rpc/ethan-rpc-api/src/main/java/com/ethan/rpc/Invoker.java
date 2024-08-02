package com.ethan.rpc;

import com.ethan.common.Node;

/**
 * Invoker, a executable service calls (API/SPI, Prototype, ThreadSafe).
 *
 * @author Huang Z.Y.
 */
public interface Invoker<T> extends Node {

    /**
     * Get service interface.
     *
     * @return Service interface
     */
    Class<T> getInterface();

    /**
     * Invoke.
     *
     * @param invocation Invoke body
     * @return result Invoke result
     * @throws RpcException When something goes wrong with the call
     */
    Result invoke(Invocation invocation) throws RpcException;

}
