package com.ethan.config.annotation;

import java.lang.annotation.*;

/**
 * An annotation used for referencing a Dubbo service.
 *
 * @author Huang Z.Y.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.ANNOTATION_TYPE})
public @interface Reference {

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
    String proxy() default "";

    /**
     * Timeout value for service invocation, default value is 0.
     */
    int timeout() default -1;

    /**
     * Registry associated name.
     */
    String registry() default "";

}
