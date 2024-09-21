package com.ethan.common.config;

/**
 * @author Huang Z.Y.
 */
public class ConsumerConfig extends AbstractInterfaceConfig {

    private static final long serialVersionUID = 7008400050736212669L;

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
    