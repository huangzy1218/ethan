package com.ethan.common.extension;

import com.ethan.common.lang.Prioritized;
import com.ethan.common.util.CollectionUtils;
import com.ethan.common.util.Holder;
import com.ethan.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * Load ethan extensions.
 *
 * @author Huang Z.Y.
 */
@Slf4j
public class ExtensionLoader<T> {

    private static final Pattern NAME_SEPARATOR = Pattern.compile("\\s*[,]+\\s*");

    /**
     * Directory path to store service configuration. <br/>
     * This is the agreed location of the configuration file for Ethan's extension point.
     */
    private static final String[] SERVICE_DIRECTORIES = new String[]
            {DEFAULT_EXTENSION_PATH, EXTENSION_INTERNAL_PATH, EXTENSION_SERVICE_PATH};

    private final Class<?> type;

    private final AtomicBoolean destroyed = new AtomicBoolean();

    private final ConcurrentMap<String, Holder<Object>> cachedInstances = new ConcurrentHashMap<>(16);

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final ConcurrentMap<Class<?>, Object> extensionInstances = new ConcurrentHashMap<>(64);

    ExtensionLoader(Class<?> type) {
        this.type = type;
    }

    /**
     * Find the extension with the given name.
     *
     * @throws IllegalStateException If the specified extension is not found
     */
    @SuppressWarnings("unchecked")
    public T getExtension(String name) {
        checkDestroyed();
        if (StringUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Extension name == null");
        }
        final Holder<Object> holder = getOrCreateHolder(name);
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    private Holder<Object> getOrCreateHolder(String name) {
        Holder<Object> holder = cachedInstances.get(name);
        if (holder == null) {
            // Cache the extension instance
            cachedInstances.putIfAbsent(name, new Holder<>());
            holder = cachedInstances.get(name);
        }
        return holder;
    }

    @SuppressWarnings("unchecked")
    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            // Can't find the extension name
            throw new IllegalStateException("No such extension " + type.getName() + " by name");
        }
        try {
            // Get the instance from cache
            T instance = (T) extensionInstances.get(clazz);
            if (instance == null) {
                extensionInstances.putIfAbsent(clazz, createExtensionInstance(clazz));
                instance = (T) extensionInstances.get(clazz);
            }

            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException(
                    "Extension instance (name: " + name + ", class: " + type + ") couldn't be instantiated: "
                            + t.getMessage(),
                    t);
        }
    }

    @SuppressWarnings("unchecked")
    private T createExtensionInstance(Class<?> clazz) {
        try {
            // Create an instance of a class by using reflection
            return (T) clazz.getDeclaredConstructor().newInstance();
        } catch (Throwable t) {
            throw new IllegalStateException("Could not instantiate extension class: " + clazz, t);
        }
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        checkDestroyed();

        // Load extension classes from configuration files
        Map<String, Class<?>> extensionClasses = new HashMap<>();
        for (String path : SERVICE_DIRECTORIES) {
            loadDirectory(extensionClasses, path);
        }

        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir) {
        // Construct configuration file
        String fileName = dir + type.getName();
        try {
            Enumeration<URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                // Get the resource path (root path)
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    URL resourceURL = urls.nextElement();
                    loadResource(extensionClasses, classLoader, resourceURL);
                }
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Exception when loading extension class(interface: " +
                    type + ", description file: " + dir + ").", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, URL resourceURL) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resourceURL.openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    try {
                        String name = null;
                        int i = line.indexOf('=');
                        if (i > 0) {
                            name = line.substring(0, i).trim();
                            line = line.substring(i + 1).trim();
                        }
                        if (!line.isEmpty()) {
                            Class<?> clazz = Class.forName(line, true, classLoader);
                            if (!type.isAssignableFrom(clazz)) {
                                throw new IllegalStateException("Error when loading extension class(interface: " +
                                        type + ", class line: " + clazz.getName() + "), class " +
                                        clazz.getName() + " is not subtype of interface.");
                            }
                            if (StringUtils.isEmpty(name)) {
                                name = findAnnotationName(clazz);
                                if (StringUtils.isNullOrEmpty(name)) {
                                    if (!clazz.getSimpleName().isEmpty()) {
                                        name = clazz.getSimpleName();
                                    } else {
                                        throw new IllegalStateException("No such extension name for the class "
                                                + clazz.getName() + " in the config " + resourceURL);
                                    }
                                }
                            }
                            String[] names = NAME_SEPARATOR.split(name);
                            for (String n : names) {
                                if (!extensionClasses.containsKey(n)) {
                                    extensionClasses.put(n, clazz);
                                }
                            }
                        }
                    } catch (Throwable t) {
                        throw new IllegalStateException("Failed to load extension class(interface: " +
                                type + ", class line: " + line + ") in " + resourceURL + ", cause: " + t.getMessage(), t);
                    }
                }
            }
        } catch (Throwable t) {
            throw new IllegalStateException("Exception when loading extension class(interface: " +
                    type + ", description file: " + resourceURL + ").", t);
        }
    }

    /**
     * Get the current thread class loader.
     *
     * @return Current thread class loader
     */
    private ClassLoader findClassLoader() {
        return Thread.currentThread().getContextClassLoader();
    }

    private String findAnnotationName(Class<?> clazz) {
        SPI spi = clazz.getAnnotation(SPI.class);
        return (spi == null) ? null : spi.value();
    }

    private void checkDestroyed() {
        if (destroyed.get()) {
            throw new IllegalStateException("ExtensionLoader is destroyed: " + type);
        }
    }

    public Set<T> getSupportedExtensionInstances() {
        checkDestroyed();
        List<T> instances = new LinkedList<>();
        Set<String> supportedExtensions = getSupportedExtensions();
        if (CollectionUtils.isNotEmpty(supportedExtensions)) {
            for (String name : supportedExtensions) {
                instances.add(getExtension(name));
            }
        }
        // sort the Prioritized instances
        instances.sort(Prioritized.COMPARATOR);
        return new LinkedHashSet<>(instances);
    }

    public Set<String> getSupportedExtensions() {
        checkDestroyed();
        Map<String, Class<?>> classes = getExtensionClasses();
        return Collections.unmodifiableSet(new TreeSet<>(classes.keySet()));
    }

}
