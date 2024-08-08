package com.ethan.common.config;

import lombok.Data;
import lombok.ToString;

/**
 * Registry configuration.
 *
 * @author Huang Z.Y.
 */
@Data
@ToString
public class RegistryConfig extends AbstractConfig {

    public static final String NO_AVAILABLE = "N/A";
    private static final long serialVersionUID = -7817082142294164305L;

    /**
     * Protocol for register center.
     */
    private String protocol;

    /**
     * Register center host.
     */
    private String host;

    /**
     * Default port for register center.
     */
    private Integer port;

    /**
     * Username to login register center.
     */
    private String username;

    /**
     * Password to login register center.
     */
    private String password;

}
