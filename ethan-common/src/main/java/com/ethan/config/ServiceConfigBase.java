package com.ethan.config;

/**
 * Base service configuration.
 *
 * @author Huang Z.Y.
 */
public abstract class ServiceConfigBase<T> extends AbstractInterfaceConfig {

    /**
     * The interface name of the exported service.
     */
    protected String interfaceName;

    /**
     * Strategies for generating dynamic agents，there are two strategies can be chosen: jdk and javassist.
     */
    protected String proxy;

    /**
     * The interface class of the exported service
     */
    protected Class<?> interfaceClass;

    /**
     * The reference of the interface implementation.
     */
    protected transient T ref;

    /**
     * The service name.
     */
    protected String path;

    /**
     * The provider configuration.
     */
    protected ProviderConfig provider;

    /**
     * Export service to network.
     */
    public abstract void export();

    public abstract boolean isExported();

}
    