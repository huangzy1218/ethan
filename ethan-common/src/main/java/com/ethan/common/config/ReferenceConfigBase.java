package com.ethan.common.config;

/**
 * @author Huang Z.Y.
 */
public class ReferenceConfigBase<T> extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 7173031481212594384L;

    /**
     * The interface class of the exported service
     */
    protected Class<?> interfaceClass;
    /**
     * The consumer config (default).
     */
    protected ConsumerConfig consumer;

}
