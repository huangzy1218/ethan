package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.AbstractInterfaceConfig;
import com.ethan.remoting.transport.netty.server.NettyServer;
import com.ethan.rpc.Exporter;
import com.ethan.rpc.ProxyFactory;
import com.ethan.rpc.model.ApplicationModel;
import com.ethan.rpc.model.FrameworkServiceRepository;
import lombok.Data;
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
    private NettyServer server;

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
        repository.registerService();
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

}
