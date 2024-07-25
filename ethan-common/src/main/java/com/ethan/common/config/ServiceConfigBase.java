package com.ethan.common.config;

import com.ethan.common.util.StringUtils;

/**
 * Base service configuration.
 *
 * @author Huang Z.Y.
 */
public abstract class ServiceConfigBase<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = -1231826725639731091L;
    
    /**
     * The interface class of the exported service
     */
    protected Class<?> interfaceClass;

    /**
     * The reference of the interface implementation.
     */
    protected transient T ref;

    /**
     * The provider configuration.
     */
    protected ProviderConfig provider;

    /**
     * Export service to network.
     */
    public abstract void export();

    public abstract boolean isExported();

    public Class<?> getInterfaceClass() {
        if (interfaceClass != null) {
            return interfaceClass;
        }
        try {
            if (StringUtils.isNotEmpty(interfaceName)) {
                this.interfaceClass = Class.forName(
                        interfaceName, true, Thread.currentThread().getContextClassLoader());
            }
        } catch (ClassNotFoundException t) {
            throw new IllegalStateException(t.getMessage(), t);
        }
        return interfaceClass;
    }

}
