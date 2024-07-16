package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.ProtocolConfig;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.descriptor.ServiceDescriptor;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.model.FrameworkServiceRepository;
import com.ethan.rpc.model.ProviderModel;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static com.ethan.common.constant.CommonConstants.LOCALHOST_VALUE;
import static com.ethan.common.constant.CommonConstants.LOCAL_PROTOCOL;

/**
 * Service configuration, which encapsulates service information.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ServiceConfig<T> extends ServiceConfigBase<T> {

    /**
     * The exported services.
     */
    private final List<Exporter<?>> exporters = new CopyOnWriteArrayList<>();
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

    private void exportLocal(URL url) {
        URL local = URLBuilder.from(url)
                .setProtocol(LOCAL_PROTOCOL)
                .setHost(LOCALHOST_VALUE)
                .setPort(0)
                .build();
        local = local.setServiceModel(providerModel);
        local = local.addParameter(EXPORTER_LISTENER_KEY, LOCAL_PROTOCOL);
        doExportUrl(local, false, RegisterTypeEnum.AUTO_REGISTER);
        log.info("Export dubbo service " + interfaceClass.getName() + " to local registry url : " + local);
    }

    @Override
    public boolean isExported() {
        return exported;
    }

}
