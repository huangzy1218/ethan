package com.ethan.rpc.protocol.filter;

import com.ethan.rpc.Invocation;
import com.ethan.rpc.Invoker;
import com.ethan.rpc.Result;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.protocol.Filter;
import com.ethan.rpc.protocol.remote.RetryInvoker;

/**
 * @author Huang Z.Y.
 */
public class RetryFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RetryInvoker<?> retryInvoker = new RetryInvoker<>(invoker);
        return retryInvoker.invoke(invocation);
    }

}
    