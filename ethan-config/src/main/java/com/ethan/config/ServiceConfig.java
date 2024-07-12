package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.descriptor.ServiceDescriptor;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.model.FrameworkServiceRepository;
import com.ethan.rpc.model.ProviderModel;

import java.util.List;

/**
 * Service configuration, which encapsulates service information.
 *
 * @author Huang Z.Y.
 */
public class ServiceConfig<T> extends ServiceConfigBase<T> {

    /**
     * A {@link com.ethan.rpc.ProxyFactory} implementation that will generate a exported service proxy,
     * the JavassistProxyFactory is its default implementation.
     */
    private ProxyFactory proxyFactory;

    private ProviderModel providerModel;

    /**
     * Whether the provider has been exported.
     */
    private transient volatile boolean exported;

    @Override
    public void export() {
        synchronized (this) {
            if (this.exported) {
                return;
            }
            doExport();
        }
    }

    private void doExport() {
        if (exported) {
            return;
        }

        FrameworkServiceRepository repository = ApplicationModel.getServiceRepository();
        ServiceDescriptor serviceDescriptor = repository.registerService(getInterfaceClass());
        providerModel = new ProviderModel(serviceKey, ref);

        repository.registerProvider(providerModel);

        List<URL> registryURLs = ConfigValidationUtils.loadRegistries(this, true);

        for (ProtocolConfig protocolConfig : protocols) {
            String pathKey = URL.buildKey(
                    getContextPath(protocolConfig).map(p -> p + "/" + path).orElse(path), group, version);
            // stub service will use generated service name
            if (!serverService) {
                // In case user specified path, register service one more time to map it to path.
                repository.registerService(pathKey, interfaceClass);
            }
            doExportUrlsFor1Protocol(protocolConfig, registryURLs, registerType);
        }

        providerModel.setServiceUrls(urls);

    }

    @Override
    public boolean isExported() {
        return exported;
    }

}
