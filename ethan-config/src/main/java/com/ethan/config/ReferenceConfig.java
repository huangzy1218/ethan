package com.ethan.config;

import com.ethan.common.config.AbstractInterfaceConfig;
import com.ethan.common.config.ConsumerConfig;
import com.ethan.model.ConsumerModel;
import com.ethan.rpc.Invoker;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @author Huang Z.Y.
 */
public class ReferenceConfig<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 1505896195123513822L;
    private final AnnotationConfigApplicationContext CONTEXT = new AnnotationConfigApplicationContext(ReferenceConfig.class);
    /**
     * The interface class of the reference service.
     */
    protected Class<?> interfaceClass;
    /**
     * The url for peer-to-peer invocation.
     */
    protected String url;
    /**
     * The consumer config (default).
     */
    protected ConsumerConfig consumer;
    private ConsumerModel consumerModel;
    /**
     * The interface proxy reference.
     */
    private transient volatile T ref;
    /**
     * The invoker of the reference service.
     */
    private transient volatile Invoker<?> invoker;
    /**
     * whether this ReferenceConfig has been destroyed.
     */
    private transient volatile boolean destroyed;
    /**
     * The flag whether the ReferenceConfig has been initialized.
     */
    private transient volatile boolean initialized;

    public ReferenceConfig() {
        super();
    }

    @SuppressWarnings("unchecked")
    public T get() {
        return (T) CONTEXT.getBean(interfaceClass.getName());
    }

}
