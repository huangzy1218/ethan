package com.ethan.config;

import com.ethan.common.URL;
import com.ethan.common.config.AbstractInterfaceConfig;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

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
     * The interface class of the exported service
     */
    protected Class<?> interfaceClass;
    /**
     * The reference of the interface implementation.
     */
    protected transient T ref;
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
        // todo
//        ServiceRepository repository = ApplicationModel.getServiceRepository();
//        repository.registerService(interfaceName, ref);
    }

    private void exportLocal(URL url) {

    }

}
