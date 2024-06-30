package com.ethan.rpc;

import java.io.Serial;
import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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
    private String serviceKey;
    private transient Class<?>[] parameterTypes;
    private transient Object[] parameters;
    private transient Class<?> returnType;
    /**
     * Only used on the caller side, will not appear on the wire.
     */
    private transient Map<Object, Object> attributes = Collections.synchronizedMap(new HashMap<>());

    public RpcInvocation(String methodName, String interfaceName,
                         String serviceKey, Class<?>[] parameterTypes,
                         Object[] parameters) {
        this.methodName = methodName;
        this.interfaceName = interfaceName;
        this.serviceKey = serviceKey;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getServiceName() {
        return serviceKey;
    }

    @Override
    public Class<?>[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public Object[] getParameters() {
        return parameters;
    }

    @Override
    public Object get(Object key) {
        return attributes.get(key);
    }

    @Override
    Object put(Object key, Object value) {
        return attributes.put(key, value);
    }

}
