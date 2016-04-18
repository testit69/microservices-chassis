package com.oreilly.microservices.health.checks;

import com.google.inject.AbstractModule;
import com.google.inject.Scopes;

public class ChecksModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(LowDiskSpaceHealthCheck.class).in(Scopes.SINGLETON);
    }
}
