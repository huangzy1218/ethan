package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.Environment;
import com.ethan.common.url.component.URLAddress;
import com.ethan.common.url.component.URLParam;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;
import com.ethan.registry.RegistryFactory;
import com.ethan.remoting.RemotingException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * Service repository for framework.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ServiceRepository {

    /**
     * Services.
     */
    private final ConcurrentMap<String, Object> services = new ConcurrentHashMap<>();
    public Registry registry;
    Environment environment = ApplicationModel.defaultModel().modelEnvironment();

    public Object registerService(ServiceConfig<?> serviceConfig) throws RemotingException {
        try {
            RegistryFactory registryFactory =
                    ApplicationModel.defaultModel().getExtensionLoader(RegistryFactory.class).getExtension(DEFAULT_REGISTRY);
            registry = registryFactory.createRegistry(buildRegistryURL());
            URL serviceURL = createServiceURL(serviceConfig);
            registry.register(serviceURL);
        } catch (Exception e) {
            log.error("Failed to register service: {}", serviceConfig.getInterfaceName(), e);
            throw new RemotingException(e);
        }
        return registerService(serviceConfig.getInterfaceName(), serviceConfig.getRef());
    }

    private URL createServiceURL(ServiceConfig<?> serviceConfig) {
        URL url = new URL();
        url.setPort(DEFAULT_SERVER_PORT);
        url.setInterfaceName(serviceConfig.getInterfaceName());
        url.addParameter(GROUP_KEY, serviceConfig.getGroup())
                .addParameter(VERSION_KEY, serviceConfig.getVersion());
        return url;
    }

    protected <T> T registerService(String interfaceName, T serviceRef) {
        services.put(interfaceName, serviceRef);
        return serviceRef;
    }

    @SuppressWarnings("unchecked")
    public <T> T getService(String interfaceName) {
        return (T) services.get(interfaceName);
    }

    private URL buildRegistryURL() {
        String host = (String) environment.getProperty(REGISTRY_HOST);
        int port = (Integer) environment.getProperty(REGISTRY_PORT);
        return URL.builder()
                .urlAddress(new URLAddress(DEFAULT_REGISTRY, host, port))
                .urlParam(new URLParam()).build();
    }

}
    