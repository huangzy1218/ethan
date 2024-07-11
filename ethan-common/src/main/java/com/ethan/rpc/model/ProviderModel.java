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

    public ProviderModel(Object proxyObject, String serviceKey, Object serviceInstance) {
        super(proxyObject, serviceKey);
    }


    public ProviderModel(
            String serviceKey,
            Object serviceInstance) {
        super(serviceInstance, serviceKey);
        if (serviceInstance == null) {
            throw new IllegalArgumentException("Service[" + serviceKey + "]Target is NULL.");
        }
    }

    public Object getServiceInstance() {
        return getProxyObject();
    }

}
