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
     * Service group, default value is empty string.
     */
    String group() default "";

    /**
     * Service version, default value is empty string.
     */
    String version() default "";

}
