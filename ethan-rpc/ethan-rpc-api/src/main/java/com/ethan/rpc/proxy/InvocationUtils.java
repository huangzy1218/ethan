package com.ethan.rpc.proxy;

import com.ethan.rpc.Invoker;
import com.ethan.rpc.Result;
import com.ethan.rpc.RpcException;
import com.ethan.rpc.RpcInvocation;

/**
 * RPC invocation utility.
 *
 * @author Huang Z.Y.
 */
public class InvocationUtils {

    public static Object invoke(Invoker<?> invoker, RpcInvocation invocation) throws RpcException {
        Result result;
        try {
            // Invoke invoker's Invoke method to perform the RPC call
            result = invoker.invoke(invocation);
        } catch (RpcException e) {
            // Handle exceptions during RPC calls
            throw e;
        }

        // Return call result
        return result.getValue();
    }

}
    