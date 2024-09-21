package com.ethan.rpc.proxy;

import com.ethan.rpc.Invoker;
import com.ethan.rpc.RpcInvocation;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Invocation handler.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class InvokerInvocationHandler implements InvocationHandler {

    private final Invoker<?> invoker;
    private final String protocolServiceKey;

    public InvokerInvocationHandler(Invoker<?> handler) {
        this.invoker = handler;
        this.protocolServiceKey = invoker.getInterface().getName();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (method.getDeclaringClass() == Object.class) {
            return method.invoke(invoker, args);
        }
        String methodName = method.getName();
        Class<?>[] parameterTypes = method.getParameterTypes();
        if (parameterTypes.length == 0) {
            if ("toString".equals(methodName)) {
                return invoker.toString();
            } else if ("hashCode".equals(methodName)) {
                return invoker.hashCode();
            }
        } else if (parameterTypes.length == 1 && "equals".equals(methodName)) {
            return invoker.equals(args[0]);
        }
        RpcInvocation rpcInvocation = new RpcInvocation(
                method.getName(),
                invoker.getInterface().getName(),
                protocolServiceKey,
                method.getParameterTypes(),
                args);

        return InvocationUtils.invoke(invoker, rpcInvocation);
    }

}
    