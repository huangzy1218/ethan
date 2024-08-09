package com.ethan.config.support;

import com.ethan.common.extension.ExtensionLoader;
import com.ethan.model.ApplicationModel;
import com.ethan.registry.Registry;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.common.constant.CommonConstants.DEFAULT_REGISTRY;

/**
 * @author Huang Z.Y.
 */
public class RegistrySupport {

    private static final Map<String, Registry> NAME_REGISTRY_MAP = new ConcurrentHashMap<>();

    static {
        ExtensionLoader<Registry> registryExtensionLoader =
                ApplicationModel.defaultModel().getExtensionLoader(Registry.class);
        Set<String> registrySupportedExtensions = registryExtensionLoader.getSupportedExtensions();
        for (String name : registrySupportedExtensions) {
            Registry registry = registryExtensionLoader.getExtension(name);
            NAME_REGISTRY_MAP.put(name, registry);
        }
    }

    public static Registry getRegistry(String name) {
        return NAME_REGISTRY_MAP.get(name);
    }

    public static Registry getRegistry(ServiceConfig<?> serviceConfig) {
        // fixme: No flexible
        return ApplicationModel.defaultModel()
                .getExtensionLoader(Registry.class)
                .getExtension(DEFAULT_REGISTRY);
    }

}
