package com.ethan.common.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Registry configuration.
 *
 * @author Huang Z.Y.
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class RegistryConfig extends AbstractConfig {

    public static final String NO_AVAILABLE = "N/A";
    private static final long serialVersionUID = -7817082142294164305L;
    
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
