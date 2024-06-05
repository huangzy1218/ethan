package com.ethan.common.context;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Get bean from application context with annotation {@link org.springframework.stereotype.Component @Component}.
 *
 * @author Huang Z.Y.
 */
@Configuration
@ComponentScan(basePackages = {"com.ethan"})
public class ApplicationContextProvider {

    private static AnnotationConfigApplicationContext context;

    protected ApplicationContextProvider() {
        // There has some flaw in order to simplify the code.
        // Configuration class must provide visible constructor but universally utility can not
        // be instantiated.
    }

    // Initialize with a configuration class
    public static void initialize() {
        context = new AnnotationConfigApplicationContext(ApplicationContextProvider.class);
    }


    public static <T> T getBean(Class<T> beanClass) {
        if (context == null) {
            initialize();
        }
        return context.getBean(beanClass);
    }

}
