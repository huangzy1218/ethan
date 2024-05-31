package com.ethan.rpc;

import java.io.Serial;
import java.io.Serializable;

/**
 * RPC Invocation, can be understood as a client request.
 *
 * @author Huang Z.Y.
 */
public class RpcInvocation implements Invocation, Serializable {

    @Serial
    private static final long serialVersionUID = 2691386627L;

    private String methodName;

    private String interfaceName;

    private String serviceName;

    private transient Class<?>[] parameterTypes;

    private transient Object[] parameters;

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

}
