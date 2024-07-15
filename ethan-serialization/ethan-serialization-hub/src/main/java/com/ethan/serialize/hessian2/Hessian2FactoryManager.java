package com.ethan.serialize.hessian2;

import com.alibaba.com.caucho.hessian.io.SerializerFactory;

/**
 * Hessian2 Factory Manager.
 *
 * @author Huang Z.Y.
 */
public class Hessian2FactoryManager {

    private volatile SerializerFactory SYSTEM_SERIALIZER_FACTORY;
    /**
     * Cache a SerializerFactory instance.
     */
    private volatile SerializerFactory stickySerializerFactory = null;

    public SerializerFactory getSerializerFactory() {
        SerializerFactory sticky = stickySerializerFactory;
        if (sticky != null) {
            return sticky;
        }

        // System classloader
        if (SYSTEM_SERIALIZER_FACTORY == null) {
            synchronized (this) {
                if (SYSTEM_SERIALIZER_FACTORY == null) {
                    SYSTEM_SERIALIZER_FACTORY = createSerializerFactory();
                }
            }
        }
        return SYSTEM_SERIALIZER_FACTORY;
    }

    private SerializerFactory createSerializerFactory() {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        Hessian2SerializerFactory hessian2SerializerFactory =
                new Hessian2SerializerFactory(classLoader);
        hessian2SerializerFactory.setAllowNonSerializable(
                Boolean.parseBoolean(System.getProperty("ethan.hessian.allowNonSerializable", "false")));
        hessian2SerializerFactory.getClassFactory().allow("com.ethan.*");
        return hessian2SerializerFactory;
    }

}
