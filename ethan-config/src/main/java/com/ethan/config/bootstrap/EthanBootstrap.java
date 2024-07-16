package com.ethan.config.bootstrap;

import com.ethan.common.config.ApplicationConfig;
import com.ethan.common.config.ConfigManager;
import com.ethan.common.util.ConcurrentHashMapUtils;
import com.ethan.config.bootstrap.builder.ApplicationBuilder;
import com.ethan.rpc.model.ApplicationModel;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Consumer;

/**
 * The bootstrap class of Ethan.<br/>
 * Get singleton instance by calling static method {@link #getInstance()}.
 * Designed as singleton because some classes inside Ethan, such as {@link com.ethan.common.extension.ExtensionLoader}, are designed only for one instance
 * per process.
 *
 * @author Huang Z.Y.
 */
public final class EthanBootstrap {

    private static final ConcurrentMap<ApplicationModel, EthanBootstrap> instanceMap = new ConcurrentHashMap<>();
    private static volatile EthanBootstrap instance;
    private final ApplicationModel applicationModel;
    private final ConfigManager configManager;
    private final Object startLock = new Object();
    protected volatile boolean initialized = false;

    public EthanBootstrap(ApplicationModel applicationModel) {
        this.applicationModel = applicationModel;
        configManager = applicationModel.getApplicationConfigManager();
    }

    public static EthanBootstrap getInstance() {
        if (instance == null) {
            synchronized (EthanBootstrap.class) {
                if (instance == null) {
                    instance = EthanBootstrap.getInstance(ApplicationModel.defaultModel());
                }
            }
        }
        return instance;
    }

    public static EthanBootstrap getInstance(ApplicationModel applicationModel) {
        return ConcurrentHashMapUtils.computeIfAbsent(
                instanceMap, applicationModel, k -> new EthanBootstrap(applicationModel));
    }

    /**
     * Set the name of application.
     *
     * @param name the name of application
     * @return current {@link EthanBootstrap} instance
     */
    public EthanBootstrap application(String name) {
        return application(name, builder -> {
            // DO NOTHING
        });
    }

    /**
     * Set the name of application and it's future build.
     *
     * @param name            The name of application
     * @param consumerBuilder {@link ApplicationBuilder}
     * @return Current {@link EthanBootstrap} instance
     */
    public EthanBootstrap application(String name, Consumer<ApplicationBuilder> consumerBuilder) {
        ApplicationBuilder builder = createApplicationBuilder(name);
        consumerBuilder.accept(builder);
        return application(builder.build());
    }

    private ApplicationBuilder createApplicationBuilder(String name) {
        return new ApplicationBuilder().name(name);
    }

    /**
     * Set the {@link com.ethan.common.config.ApplicationConfig}.
     *
     * @param applicationConfig The {@link com.ethan.common.config.ApplicationConfig}
     * @return Current {@link EthanBootstrap} instance
     */
    public EthanBootstrap application(ApplicationConfig applicationConfig) {
        configManager.setApplication(applicationConfig);
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
            onInitialize();

            // register shutdown hook
            registerShutdownHook();

            startConfigCenter();

            loadApplicationConfigs();

            initModuleDeployers();

            initMetricsReporter();

            initMetricsService();

            // @since 2.7.8
            startMetadataCenter();

            initialized = true;
        }
    }

    private void startConfigCenter() {
        configManager.loadConfigsOfTypeFromProps(ApplicationConfig.class);
    }

}
