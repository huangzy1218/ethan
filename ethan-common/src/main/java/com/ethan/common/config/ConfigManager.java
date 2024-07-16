package com.ethan.common.config;

import com.ethan.common.context.ApplicationExt;
import com.ethan.common.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.common.config.AbstractConfig.getTagName;

/**
 * A lock-free config manager (through ConcurrentHashMap), for fast read operation.
 *
 * @author Huang Z.Y.
 */
public class ConfigManager implements ApplicationExt {

    public static final String NAME = "config";
    private static final Class<? extends AbstractConfig>[] supportedConfigTypes = new Class[]{
            ProviderConfig.class,
            ConsumerConfig.class,
            ProtocolConfig.class,
            RegistryConfig.class,
    };

    private Map<String, AbstractConfig> configsCache = new ConcurrentHashMap<>();

    @SuppressWarnings("unchecked")
    protected <C extends AbstractConfig> C getConfigs(String configType) {
        return (C) configsCache.get(configType);
    }

    public RegistryConfig getRegistry() {
        return getConfigs(getTagName(RegistryConfig.class));
    }

    /**
     * Add the ethan {@link AbstractConfig config}.
     *
     * @param config the dubbo {@link AbstractConfig config}
     */
    @SuppressWarnings("unchecked")
    public final void addConfig(AbstractConfig config) {
        if (config == null) {
            return;
        }
        // ignore MethodConfig
        if (!isSupportConfigType(config.getClass())) {
            throw new IllegalArgumentException("Unsupported config type: " + config);
        }
        configsCache.putIfAbsent(getTagName(config.getClass()), config);
    }

    public boolean isSupportConfigType(Class<? extends AbstractConfig> type) {
        for (Class<? extends AbstractConfig> supportedConfigType : supportedConfigTypes) {
            if (supportedConfigType.isAssignableFrom(type)) {
                return true;
            }
        }
        return false;
    }

    private <C extends AbstractConfig> C addIfAbsent(C config, Map<String, C> configsMap) {
        String key = config.getId();
        if (StringUtils.isEmpty(key)) {
            key = getTagName(config.getClass());
        }
        if (!configsMap.containsKey(key)) {
            configsMap.put(key, config);
            return config;
        }
        // If present, return the existing config
        return configsMap.get(key);
    }

    public void setApplication(ApplicationConfig application) {
        addConfig(application);
    }

}
