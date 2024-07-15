package com.ethan.rpc.model;

import com.ethan.common.bean.ScopeBeanFactory;
import com.ethan.common.extension.ExtensionAccessor;
import com.ethan.common.extension.ExtensionDirector;
import com.ethan.common.util.Assert;
import com.ethan.common.util.ConcurrentHashSet;
import lombok.Getter;

import java.util.Set;

/**
 * Model of ethan application, it can be shared with multiple applications.
 *
 * @author Huang Z.Y.
 */
public class ApplicationModel implements ExtensionAccessor {

    private static final Object globalLock = new Object();
    private static volatile ApplicationModel defaultInstance;
    @Getter
    private static FrameworkServiceRepository serviceRepository = new FrameworkServiceRepository();
    private static volatile ScopeBeanFactory beanFactory = new ScopeBeanFactory();
    protected final Object instLock = new Object();
    private final Set<ClassLoader> classLoaders = new ConcurrentHashSet<>();
    private volatile ExtensionDirector extensionDirector;

    public ApplicationModel() {
        initialize();
    }

    public static ScopeBeanFactory getBeanFactory() {
        return beanFactory;
    }

    /**
     * During destroying the default ApplicationModel, the ApplicationModel.defaultModel() or ApplicationModel.defaultModel()
     * will return a broken model, maybe cause unpredictable problem.
     * Recommendation: Avoid using the default model as much as possible.
     *
     * @return The global default ApplicationModel
     */
    public static ApplicationModel defaultModel() {
        ApplicationModel instance = defaultInstance;
        if (instance == null) {
            synchronized (globalLock) {
                if (defaultInstance == null) {
                    defaultInstance = new ApplicationModel();
                }
                instance = defaultInstance;
            }
        }
        Assert.notNull(instance, "Default ApplicationModel is null");
        return instance;
    }

    /**
     * NOTE:
     * <ol>
     *  <li>The initialize method only be called in subclass.</li>
     * <li>
     * In subclass, the extensionDirector and beanFactory are available in initialize but not available in constructor.
     * </li>
     * </ol>
     */
    protected void initialize() {
        synchronized (instLock) {
            this.beanFactory = new ScopeBeanFactory(null);
            this.extensionDirector = new ExtensionDirector();
            // Add Framework's ClassLoader by default
            ClassLoader ethanClassLoader = ApplicationModel.class.getClassLoader();
            if (ethanClassLoader != null) {
                this.addClassLoader(ethanClassLoader);
            }
        }
    }

    public void addClassLoader(ClassLoader classLoader) {
        synchronized (instLock) {
            this.classLoaders.add(classLoader);
        }
    }

    @Override
    public ExtensionDirector getExtensionDirector() {
        return extensionDirector;
    }

}
