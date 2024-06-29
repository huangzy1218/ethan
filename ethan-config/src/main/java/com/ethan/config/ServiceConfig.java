package com.ethan.config;

import com.ethan.common.util.StringUtils;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.model.FrameworkServiceRepository;
import com.ethan.rpc.model.ProviderModel;

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

    /**
     * For early init service metadata.
     */
    public void init() {
        initServiceMetadata(provider);
        serviceMetadata.setTarget(ref);
        serviceMetadata.generateServiceKey();
    }


    @Override
    public void export() {
        synchronized (this) {
            if (this.exported) {
                return;
            }
            init();
            doExport();
        }
    }

    private void doExport() {
        FrameworkServiceRepository repository = new FrameworkServiceRepository();
        if (StringUtils.isEmpty(path)) {
            path = interfaceName;
        }

        providerModel = new ProviderModel(
                serviceMetadata.getServiceKey(),
                ref,
                serviceMetadata);

    }

    @Override
    public boolean isExported() {
        return false;
    }

}
