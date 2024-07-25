package com.ethan.rpc;

import lombok.Getter;
import lombok.Setter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * RPC Invocation, can be understood as a client request.
 *
 * @author Huang Z.Y.
 */
public class RpcInvocation implements Invocation {

    private static final long serialVersionUID = 5785837690188300781L;
    @Setter
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
    @Getter
    @Setter
    private transient InvokeMode invokeMode;
    /**
     * Passed to the remote server during RPC call.
     */
    private Map<String, String> attachments = new HashMap<>();

    public RpcInvocation(String methodName, String interfaceName,
                         String serviceKey, Class<?>[] parameterTypes,
                         Object[] parameters) {
        this.methodName = methodName;
        this.interfaceName = interfaceName;
        this.serviceKey = serviceKey;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.invokeMode = InvokeMode.ASYNC;
    }

    public RpcInvocation(String methodName, String interfaceName,
                         String serviceKey, Class<?>[] parameterTypes,
                         Object[] parameters, InvokeMode invokeMode) {
        this.methodName = methodName;
        this.interfaceName = interfaceName;
        this.serviceKey = serviceKey;
        this.parameterTypes = parameterTypes;
        this.parameters = parameters;
        this.invokeMode = invokeMode;
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
    public String getAttachment(String key) {
        return attachments.get(key);
    }

    @Override
    public void setAttachment(String key, String value) {
        attachments.put(key, value);
    }

    @Override
    public Object[] getArguments() {
        return parameters;
    }

    @Override
    public Object put(Object key, Object value) {
        return attributes.put(key, value);
    }

}
