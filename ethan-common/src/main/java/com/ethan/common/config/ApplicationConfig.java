package com.ethan.common.config;

import lombok.Data;

/**
 * @author Huang Z.Y.
 */
@Data
public class ApplicationConfig extends AbstractConfig {

    private static final long serialVersionUID = 2391172883122851626L;

    /**
     * Application name
     */
    private String name;

    /**
     * The application version
     */
    private String version;

    /**
     * Application owner
     */
    private String owner;

    /**
     * Application's organization (BU)
     */
    private String organization;

}
