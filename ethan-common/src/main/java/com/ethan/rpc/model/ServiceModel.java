package com.ethan.rpc.model;

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
    private final ServiceMetadata serviceMetadata;

    public ServiceModel(
            Object proxyObject,
            String serviceKey,
            ServiceMetadata serviceMetadata) {
        this.proxyObject = proxyObject;
        this.serviceKey = serviceKey;
        this.serviceMetadata = serviceMetadata;
        if (serviceMetadata != null) {
            serviceMetadata.setServiceModel(this);
        }
        this.classLoader = Thread.currentThread().getContextClassLoader();
    }

    public ServiceModel(
            Object proxyObject,
            String serviceKey,
            ServiceMetadata serviceMetadata,
            ClassLoader interfaceClassLoader) {
        this.proxyObject = proxyObject;
        this.serviceKey = serviceKey;
        this.serviceMetadata = serviceMetadata;
        if (serviceMetadata != null) {
            serviceMetadata.setServiceModel(this);
        }
        if (interfaceClassLoader != null) {
            this.classLoader = interfaceClassLoader;
        }
        if (this.classLoader == null) {
            this.classLoader = Thread.currentThread().getContextClassLoader();
        }
    }

}
