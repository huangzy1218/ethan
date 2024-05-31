package com.ethan.common.extension;

import java.lang.annotation.*;

/**
 * Marker for extension interface.
 * Use {@code Protocol} as an example, its configuration file 'META-INF/ethan/com.xxx.Protocol' is key-value pair like: <br/>
 * <pre>
 *     xxx=com.foo.XxxProtocol
 *     yyy=com.foo.YyyProtocol
 * </pre>
 *
 * @author Huang Z.Y.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface SPI {

    /**
     * Default extension name.
     */
    String value() default "";
}
