package com.ethan.config;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * The service provider default configuration.
 *
 * @author Huang Z.Y.
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ProviderConfig extends AbstractInterfaceConfig {

    @Serial
    private static final long serialVersionUID = 2827274711143680600L;

    /**
     * Service ip addresses
     * (used when there are multiple network cards available).
     */
    private String host;

    /**
     * Service port
     */
    private Integer port;

    /**
     * Thread pool
     */
    private String threadPool;

    /**
     * Consumer thread pool core thread size.
     */
    private Integer coreThreads;

    /**
     * Consumer thread pool thread size.
     */
    private Integer threads;

    /**
     * Consumer thread pool queue size.
     */
    private Integer queues;

    /**
     * Protocol codec.
     */
    private String codec;

    /**
     * Protocol compressor.
     */
    private String compressor;

    /**
     * The serialization charset.
     */
    private String charset;

}
    