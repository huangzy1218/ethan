package com.ethan.rpc.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * ProviderModel is about published services.
 *
 * @author Huang Z.Y.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderModel extends ServiceModel {

    public ProviderModel(Object proxyObject, String serviceKey, ServiceMetadata serviceMetadata, Object serviceInstance) {
        super(proxyObject, serviceKey, serviceMetadata);
    }


    public ProviderModel(
            String serviceKey,
            Object serviceInstance,
            ServiceMetadata serviceMetadata) {
        super(serviceInstance, serviceKey, serviceMetadata);
        if (serviceInstance == null) {
            throw new IllegalArgumentException("Service[" + serviceKey + "]Target is NULL.");
        }
    }

    public Object getServiceInstance() {
        return getProxyObject();
    }

}
