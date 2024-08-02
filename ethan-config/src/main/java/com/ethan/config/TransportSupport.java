package com.ethan.config;

import com.ethan.common.util.StringUtils;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;
import com.ethan.remoting.Transporter;
import com.ethan.rpc.ProxyFactory;

import static com.ethan.common.constant.CommonConstants.DEFAULT_PROXY;
import static com.ethan.common.constant.CommonConstants.DEFAULT_REGISTRY;

/**
 * Classes that provide support for registration and network transfers.
 *
 * @author Huang Z.Y.
 */
public class TransportSupport {

    private final ProxyFactory proxy = ApplicationModel.defaultModel()
            .getExtensionLoader(ProxyFactory.class).getExtension(DEFAULT_PROXY);

    public static Registry getRegistry() {
        String registry = (String) ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.registry");
        if (StringUtils.isEmpty(registry)) {
            registry = DEFAULT_REGISTRY;
        }
        return ApplicationModel.defaultModel().getExtensionLoader(Registry.class).getExtension(registry);
    }

    public static Transporter getTransporter() {
        String transporter = (String) ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.transporter");
        if (StringUtils.isEmpty(transporter)) {
            transporter = DEFAULT_REGISTRY;
        }
        return ApplicationModel.defaultModel().getExtensionLoader(Transporter.class).getExtension(transporter);
    }

    public static ProxyFactory getProxyFactory() {
        String proxyFactory = (String) ApplicationModel.defaultModel().modelEnvironment().getProperty("ethan.proxy");
        if (StringUtils.isEmpty(proxyFactory)) {
            proxyFactory = DEFAULT_PROXY;
        }
        return ApplicationModel.defaultModel().getExtensionLoader(ProxyFactory.class).getExtension(proxyFactory);
    }

}
