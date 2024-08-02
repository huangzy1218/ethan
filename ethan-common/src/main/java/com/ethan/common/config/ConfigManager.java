package com.ethan.common.config;

import com.ethan.common.util.ConfigurationUtils;
import com.ethan.common.util.StringUtils;
import com.ethan.model.ApplicationModel;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import static com.ethan.common.config.AbstractConfig.getTagName;
import static com.ethan.common.config.AbstractConfig.invokeSetter;
import static com.ethan.common.constant.CommonConstants.ETHAN;

/**
 * A lock-free config manager (through ConcurrentHashMap), for fast read operation.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ConfigManager {

    public static final String NAME = "config";
    private static final Class<? extends AbstractConfig>[] supportedConfigTypes = new Class[]{
            ProviderConfig.class,
            ConsumerConfig.class,
            ProtocolConfig.class,
            RegistryConfig.class,
            ApplicationConfig.class
    };
    private final Environment environment = ApplicationModel.defaultModel().modelEnvironment();
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

    public <T extends AbstractConfig> void loadConfigsOfTypeFromProps(Class<T> cls) {
        try {
            T config = createConfig(cls);
            Map<String, Object> configIds = getConfigIdFromProps(cls);
            for (Map.Entry<String, Object> entry : configIds.entrySet()) {
                invokeSetter(cls, config, entry.getKey(), entry.getValue());
            }
            addConfig(config);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException(
                    "Cannot assign nested class when refreshing config: "
                            + cls.getName(),
                    e);
        }
    }

    @SuppressWarnings("unchecked")
    private <V extends Object> Map<String, V> getConfigIdFromProps(Class<? extends AbstractConfig> clazz) {
        String prefix = ETHAN + "." + AbstractConfig.getTagName(clazz) + ".";
        Properties properties = environment.getProperties();
        return (Map<String, V>) ConfigurationUtils.getSubProperties(properties, prefix);
    }

    private <T extends AbstractConfig> T createConfig(Class<T> cls)
            throws ReflectiveOperationException {
        T config = cls.getDeclaredConstructor().newInstance();
        return config;
    }

    public void addReference(AbstractConfig referenceConfig) {
        addConfig(referenceConfig);
    }

    public void addService(AbstractConfig serviceConfig) {
        addConfig(serviceConfig);
    }

}
