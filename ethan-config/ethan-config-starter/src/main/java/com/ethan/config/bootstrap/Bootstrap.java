package com.ethan.config.bootstrap;

import com.ethan.common.config.ApplicationConfig;
import com.ethan.common.config.ConfigManager;
import com.ethan.common.config.Environment;
import com.ethan.common.config.RegistryConfig;
import com.ethan.common.util.ConcurrentHashMapUtils;
import com.ethan.common.util.StringUtils;
import com.ethan.config.ReferenceConfig;
import com.ethan.config.ServiceConfig;
import com.ethan.config.bootstrap.builder.ApplicationBuilder;
import com.ethan.model.ApplicationModel;
import lombok.Getter;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

import static com.ethan.common.constant.CommonConstants.CONFIG_PATH;

/**
 * The bootstrap class of Ethan.<br/>
 * Get singleton instance by calling static method {@link #getInstance()}.
 * Designed as singleton because some classes inside Ethan, such as {@link com.ethan.common.extension.ExtensionLoader}, are designed only for one instance
 * per process.
 *
 * @author Huang Z.Y.
 */
public final class Bootstrap {

    private static final ConcurrentMap<ApplicationModel, Bootstrap> instanceMap = new ConcurrentHashMap<>();
    private static volatile Bootstrap instance;
    @Getter
    private final ApplicationModel applicationModel;
    @Getter
    private final ConfigManager configManager;
    private final Object startLock = new Object();
    protected volatile boolean initialized = false;
    @Getter
    private Environment environment;

    public Bootstrap(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        configManager = applicationModel.getApplicationConfigManager();
        environment = applicationModel.modelEnvironment();
    }

    public static Bootstrap getInstance() {
        if (instance == null) {
            synchronized (Bootstrap.class) {
                if (instance == null) {
                    instance = Bootstrap.getInstance(ApplicationModel.defaultModel());
                }
            }
        }
        return instance;
    }

    public static Bootstrap getInstance(ApplicationModel applicationModel) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                instanceMap, applicationModel, k -> new Bootstrap(applicationModel));
    }

    /**
     * Set the name of application.
     *
     * @param name the name of application
     * @return current {@link Bootstrap} instance
     */
    public Bootstrap application(String name) {
        return application(name, builder -> {
            // DO NOTHING
        });
    }

    /**
     * Set the name of application and it's future build.
     *
     * @param name            The name of application
     * @param consumerBuilder {@link ApplicationBuilder}
     * @return Current {@link Bootstrap} instance
     */
    public Bootstrap application(String name, Consumer<ApplicationBuilder> consumerBuilder) {
        ApplicationBuilder builder = createApplicationBuilder(name);
        consumerBuilder.accept(builder);
        return application(builder.build());
    }

    private ApplicationBuilder createApplicationBuilder(String name) {
        return new ApplicationBuilder().name(name);
    }

    /**
     * Set the {@link ApplicationConfig}.
     *
     * @param applicationConfig The {@link ApplicationConfig}
     * @return Current {@link Bootstrap} instance
     */
    public Bootstrap application(ApplicationConfig applicationConfig) {
        configManager.setApplication(applicationConfig);
        return this;
    }

    public Bootstrap reference(ReferenceConfig<?> referenceConfig) {
        getConfigManager().addReference(referenceConfig);
        return this;
    }

    public Bootstrap service(ServiceConfig<?> serviceConfig) {
        getConfigManager().addService(serviceConfig);
        return this;
    }

    public void initialize() {
        if (initialized) {
            return;
        }

        // Ensure that the initialization is completed when concurrent calls
        synchronized (startLock) {
            if (initialized) {
                return;
            }
            try {
                PropertySource<?> propertySource = loadYamlProperties(CONFIG_PATH);
                environment.loadProperties(propertySource);
            } catch (IOException ignored) {
            }
            configManager.loadConfigsOfTypeFromProps(RegistryConfig.class);
            initialized = true;
        }
    }

    private PropertySource<?> loadYamlProperties(String resourcePath) throws IOException {
        Resource resource = new ClassPathResource(resourcePath);
        YamlPropertySourceLoader sourceLoader = new YamlPropertySourceLoader();
        List<PropertySource<?>> yamlProperties = sourceLoader.load(StringUtils.getFilenameExtension(resourcePath), resource);
        return yamlProperties.isEmpty() ? null : yamlProperties.get(0);
    }

}
