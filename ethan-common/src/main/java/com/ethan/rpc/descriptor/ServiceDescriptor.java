package com.ethan.rpc.descriptor;

import java.util.List;
import java.util.Set;

/**
 * Merge {@link com.ethan.rpc.model.ServiceModel} and {@link com.ethan.rpc.model.ServiceMetadata}.
 *
 * @author Huang Z.Y.
 */
public interface ServiceDescriptor {

    String getInterfaceName();

    Class<?> getServiceInterfaceClass();

    Set<MethodDescriptor> getAllMethods();

    List<MethodDescriptor> getMethods(String methodName);

}
    