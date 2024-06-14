package com.ethan.rpc.model;

import com.ethan.common.URL;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * ProviderModel is about published services.
 *
 * @author Huang Z.Y.
 */
@Data
public class ProviderModel extends ServiceModel {

    /**
     * The url of the reference service
     */
    private List<URL> serviceUrls = new ArrayList<>();

    public ProviderModel(Object proxyObject, String serviceKey, ServiceMetadata serviceMetadata, Object serviceInstance) {
        super(proxyObject, serviceKey, serviceMetadata);
    }

    public Object getServiceInstance() {
        return getProxyObject();
    }

}
