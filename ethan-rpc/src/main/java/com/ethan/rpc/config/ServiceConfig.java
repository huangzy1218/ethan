package com.ethan.rpc.config;

import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.model.ProviderModel;

/**
 * Service configuration, which encapsulates service information.
 *
 * @author Huang Z.Y.
 */
public class ServiceConfig {

    /**
     * A {@link com.ethan.rpc.ProxyFactory} implementation that will generate a exported service proxy,
     * the JavassistProxyFactory is its default implementation.
     */
    private ProxyFactory proxyFactory;

    private ProviderModel providerModel;
    
}
