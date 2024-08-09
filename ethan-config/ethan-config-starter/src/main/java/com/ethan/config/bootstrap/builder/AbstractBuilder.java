package com.ethan.config.bootstrap.builder;

import com.ethan.common.config.AbstractConfig;

/**
 * @author Huang Z.Y.
 */
public abstract class AbstractBuilder<C extends AbstractConfig, B extends AbstractBuilder> {

    /**
     * The config id
     */
    protected String id;

    public B id(String id) {
        this.id = id;
        return getThis();
    }

    protected abstract B getThis();

    public abstract C build();

}
