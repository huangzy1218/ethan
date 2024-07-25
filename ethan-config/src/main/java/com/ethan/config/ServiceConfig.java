package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.ServiceConfigBase;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.descriptor.ServiceDescriptor;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.model.FrameworkServiceRepository;
import com.ethan.rpc.model.ProviderModel;
import lombok.Getter;
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
@Getter
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
        log.info("Export dubbo service {} to local registry url : {}", interfaceClass.getName(), local);
    }

    @Override
    public boolean isExported() {
        return exported;
    }

}
