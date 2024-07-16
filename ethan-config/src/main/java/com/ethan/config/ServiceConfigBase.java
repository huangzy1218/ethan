//package com.ethan.config;
//
//import com.ethan.common.config.AbstractInterfaceConfig;
//import com.ethan.common.config.ProviderConfig;
//import com.ethan.common.util.StringUtils;
//
///**
// * Base service configuration.
// *
// * @author Huang Z.Y.
// */
//public abstract class ServiceConfigBase<T> extends AbstractInterfaceConfig {
//
//    /**
//     * The interface name of the exported service.
//     */
//    protected String interfaceName;
//
//    /**
//     * Strategies for generating dynamic agents，there are two strategies can be chosen: jdk and javassist.
//     */
//    protected String proxy;
//
//    /**
//     * The interface class of the exported service
//     */
//    protected Class<?> interfaceClass;
//
//    /**
//     * The reference of the interface implementation.
//     */
//    protected transient T ref;
//
//    /**
//     * The provider configuration.
//     */
//    protected ProviderConfig provider;
//
//    /**
//     * Export service to network.
//     */
//    public abstract void export();
//
//    public abstract boolean isExported();
//
//    public Class<?> getInterfaceClass() {
//        if (interfaceClass != null) {
//            return interfaceClass;
//        }
//        try {
//            if (StringUtils.isNotEmpty(interfaceName)) {
//                this.interfaceClass = Class.forName(
//                        interfaceName, true, Thread.currentThread().getContextClassLoader());
//            }
//        } catch (ClassNotFoundException t) {
//            throw new IllegalStateException(t.getMessage(), t);
//        }
//        return interfaceClass;
//    }
//
//}
//