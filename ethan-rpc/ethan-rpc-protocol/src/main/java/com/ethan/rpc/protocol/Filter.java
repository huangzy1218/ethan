package com.ethan.rpc.protocol;

import com.ethan.common.extension.SPI;
import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Result;
import com.ethan.rpc.RpcException;

/**
 * @author Huang Z.Y.
 */
@SPI
public interface Filter {

    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}