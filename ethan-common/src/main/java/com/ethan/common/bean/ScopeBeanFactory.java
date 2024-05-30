package com.ethan.common.bean;

import com.ethan.common.util.ConcurrentHashMapUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * A bean factory for internal sharing.
 *
 * @author Huang Z.Y.
 */
public class ScopeBeanFactory {

    private final ScopeBeanFactory parent;

    private final AtomicBoolean destroyed = new AtomicBoolean();

    private final List<BeanInfo> registeredBeanInfos = new CopyOnWriteArrayList<>();

    private final ConcurrentHashMap<Class<?>, AtomicInteger> beanNameIdCounterMap = new ConcurrentHashMap<>();

    public ScopeBeanFactory(ScopeBeanFactory parent) {
        this.parent = parent;
    }

    public void registerBean(Object bean) {
        this.registerBean(null, bean);
    }

    public void registerBean(String name, Object bean) {
        checkDestroyed();
        // Avoid duplicated register same bean
        if (containsBean(name, bean)) {
            return;
        }

        Class<?> beanClass = bean.getClass();
        if (name == null) {
            name = beanClass.getName() + "#" + getNextId(beanClass);
        }

        registeredBeanInfos.add(new BeanInfo(name, bean));
    }

    private int getNextId(Class<?> beanClass) {
        return ConcurrentHashMapUtils.computeIfAbsent(beanNameIdCounterMap, beanClass, key -> new AtomicInteger())
                .incrementAndGet();
    }

    private boolean containsBean(String name, Object bean) {
        for (BeanInfo beanInfo : registeredBeanInfos) {
            if (beanInfo.instance == bean) {
                return true;
            }
        }
        return false;
    }

    public <T> T getBean(Class<T> type) {
        return this.getBean(null, type);
    }

    /**
     * Retrieve a bean of the specified name and type from this container.
     *
     * @param name The name of the bean to retrieve
     * @param type The type of the bean to retrieve
     * @return The bean instance, or {@code null} if not found
     */
    public <T> T getBean(String name, Class<T> type) {
        T bean = getBeanInternal(name, type);
        if (bean == null && parent != null) {
            return parent.getBean(name, type);
        }
        return bean;
    }

    private <T> T getBeanInternal(String name, Class<T> type) {
        checkDestroyed();
        // All classes are derived from java.lang.Object, cannot filter bean by it
        if (type == Object.class) {
            return null;
        }
        List<BeanInfo> candidates = null;
        BeanInfo firstCandidate = null;
        for (BeanInfo beanInfo : registeredBeanInfos) {
            // If required bean type is same class/superclass/interface of the registered bean
            if (type.isAssignableFrom((Class<?>) beanInfo.instance.getClass())) {
                // Optimize for only one matched bean
                if (firstCandidate == null) {
                    firstCandidate = beanInfo;
                } else {
                    if (candidates == null) {
                        candidates = new ArrayList<>();
                        candidates.add(firstCandidate);
                    }
                    candidates.add(beanInfo);
                }
            }
        }

        // If bean name not matched and only single candidate
        if (candidates != null) {
            if (candidates.size() == 1) {
                return (T) candidates.get(0).instance;
            } else if (candidates.size() > 1) {
                List<String> candidateBeanNames =
                        candidates.stream().map(beanInfo -> beanInfo.name).collect(Collectors.toList());
                throw new ScopeBeanException("expected single matching bean but found " + candidates.size()
                        + " candidates for type [" + type.getName() + "]: " + candidateBeanNames);
            }
        } else if (firstCandidate != null) {
            return (T) firstCandidate.instance;
        }
        return null;
    }

    private void checkDestroyed() {
        if (destroyed.get()) {
            throw new IllegalStateException("ScopeBeanFactory is destroyed");
        }
    }

    static class BeanInfo {
        private final String name;
        private final Object instance;

        public BeanInfo(String name, Object instance) {
            this.name = name;
            this.instance = instance;
        }
    }

}
