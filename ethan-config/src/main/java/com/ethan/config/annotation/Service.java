package com.ethan.config.annotation;

import java.lang.annotation.*;

import static com.ethan.common.constant.CommonConstants.*;

/**
 * @author Huang Z.Y.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Inherited
public @interface Service {

    /**
     * Interface class, default value is void.class
     */
    Class<?> interfaceClass() default void.class;

    /**
     * Service group, default value is empty string.
     */
    String group() default "";

    /**
     * Service version, default value is empty string.
     */
    String version() default "";

    /**
     * Whether to enable async invocation, default value is false.
     */
    boolean async() default false;

    /**
     * How the proxy is generated, legal values include: jdk, javassist
     */
    String proxy() default DEFAULT_PROXY;

    /**
     * Timeout value for service invocation, default value is 0.
     */
    int timeout() default DEFAULT_TIMEOUT;

    /**
     * Registry associated name.
     */
    String registry() default DEFAULT_REGISTRY;
    
}
