package com.ethan.common.extension;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to automatically activate certain filters or extensions.
 *
 * @author Huang Z.Y.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface Activate {

    /**
     * The group in which this extension should be activated.
     * Common values include "provider" or "consumer".
     *
     * @return The groups for which this extension is active.
     */
    String[] group() default {};

    /**
     * The specific keys or conditions that activate this extension.
     *
     * @return Activation conditions
     */
    String[] value() default {};
}
