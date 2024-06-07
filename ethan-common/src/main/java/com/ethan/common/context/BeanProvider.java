package com.ethan.common.context;

import com.ethan.common.config.CommonConfiguration;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * Get bean from application context with annotation {@link org.springframework.stereotype.Component @Component}.
 *
 * @author Huang Z.Y.
 */

public class BeanProvider {

    /**
     * Get bean with annotation of component.
     */
    private static AnnotationConfigApplicationContext context;

    private BeanProvider() {
        // There has some flaw in order to simplify the code.
        // Configuration class must provide visible constructor but universally utility can not
        // be instantiated.
    }

    // Initialize with a configuration class
    public static void initialize() {
        context = new AnnotationConfigApplicationContext(CommonConfiguration.class);
    }


    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            initialize();
        }
        return context.getBean(beanClass);
    }

}
