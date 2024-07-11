package com.ethan.rpc.descriptor;

import java.util.*;

/**
 * @author Huang Z.Y.
 */
public class ReflectionServiceDescriptor implements ServiceDescriptor {

    private final String interfaceName;
    private final Class<?> serviceInterfaceClass;
    private final Map<String, List<MethodDescriptor>> methods = new HashMap<>();

    public ReflectionServiceDescriptor(String interfaceName, Class<?> interfaceClass) {
        this.interfaceName = interfaceName;
        this.serviceInterfaceClass = interfaceClass;
    }

    public ReflectionServiceDescriptor(Class<?> interfaceClass) {
        this.serviceInterfaceClass = interfaceClass;
        this.interfaceName = interfaceClass.getName();
        // todo
        initMethods();
    }

    @Override
    public String getInterfaceName() {
        return interfaceName;
    }

    @Override
    public Class<?> getServiceInterfaceClass() {
        return serviceInterfaceClass;
    }

    @Override
    public Set<MethodDescriptor> getAllMethods() {
        Set<MethodDescriptor> methodModels = new HashSet<>();
        methods.forEach((k, v) -> methodModels.addAll(v));
        return methodModels;
    }

    @Override
    public List<MethodDescriptor> getMethods(String methodName) {
        return methods.get(methodName);
    }

    private void initMethods() {
        // todo
    }

}
    