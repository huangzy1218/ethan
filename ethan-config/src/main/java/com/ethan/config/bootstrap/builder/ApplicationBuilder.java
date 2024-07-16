package com.ethan.config.bootstrap.builder;

import com.ethan.common.config.ApplicationConfig;

/**
 * @author Huang Z.Y.
 */
public class ApplicationBuilder extends AbstractBuilder<ApplicationConfig, ApplicationBuilder> {

    private String name;
    private String version;
    private String owner;
    private String organization;

    @Override
    protected ApplicationBuilder getThis() {
        return this;
    }

    public ApplicationBuilder name(String name) {
        this.name = name;
        return getThis();
    }

    public ApplicationBuilder version(String version) {
        this.version = version;
        return getThis();
    }

    public ApplicationBuilder owner(String owner) {
        this.owner = owner;
        return getThis();
    }

    public ApplicationBuilder organization(String organization) {
        this.organization = organization;
        return getThis();
    }

    @Override
    public ApplicationConfig build() {
        ApplicationConfig config = new ApplicationConfig();

        config.setName(name);
        config.setVersion(this.version);
        config.setOwner(this.owner);
        config.setOrganization(this.organization);
        return config;
    }

}
