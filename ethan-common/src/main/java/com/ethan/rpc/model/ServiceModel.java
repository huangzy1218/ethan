package com.ethan.rpc.model;

import com.ethan.common.util.ClassUtils;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Service model.
 *
 * @author Huang Z.Y.
 */
@NoArgsConstructor(force = true)
@Data
public class ServiceModel {

    private String serviceKey;
    private Object proxyObject;
    private ClassLoader classLoader;

    public ServiceModel(
            Object proxyObject,
            String serviceKey) {
        this.proxyObject = proxyObject;
        this.serviceKey = serviceKey;
        this.classLoader = ClassUtils.getClassLoader();
    }

    public ServiceModel(
            Object proxyObject,
            String serviceKey,
            ClassLoader interfaceClassLoader) {
        this.proxyObject = proxyObject;
        this.serviceKey = serviceKey;
        if (interfaceClassLoader != null) {
            this.classLoader = interfaceClassLoader;
        }
        if (this.classLoader == null) {
            this.classLoader = ClassUtils.getClassLoader();
        }
    }

}
