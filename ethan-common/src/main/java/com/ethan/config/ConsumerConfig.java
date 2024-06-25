package com.ethan.config;

import java.io.Serial;

/**
 * @author Huang Z.Y.
 */
public class ConsumerConfig extends AbstractInterfaceConfig {

    @Serial
    private static final long serialVersionUID = 6913423882496634749L;

    /**
     * Consumer thread pool type: cached, fixed, limit, eager
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

}
    