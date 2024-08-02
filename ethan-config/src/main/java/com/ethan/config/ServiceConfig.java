package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.AbstractInterfaceConfig;
import com.ethan.model.ApplicationModel;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.ProxyFactory;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Service configuration, which encapsulates service information.
 *
 * @author Huang Z.Y.
 */
@Slf4j
@Data
public class ServiceConfig<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = -788514912403041933L;
    /**
     * The exported services.
     */
    private final List<Exporter<?>> exporters = new CopyOnWriteArrayList<>();
    /**
     * The interface class of the exported service
     */
    protected Class<?> interfaceClass;
    /**
     * The reference of the interface implementation.
     */
    protected transient T ref;
    /**
     * A {@link com.ethan.rpc.ProxyFactory} implementation that will generate a exported service proxy,
     * the JavassistProxyFactory is its default implementation.
     */
    private ProxyFactory proxyFactory;
    /**
     * Whether the provider has been exported.
     */
    private transient volatile boolean exported;

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
        ServiceRepository repository = ApplicationModel.getServiceRepository();
        repository.registerService(interfaceName, ref);
    }

    private void exportLocal(URL url) {

    }

}
