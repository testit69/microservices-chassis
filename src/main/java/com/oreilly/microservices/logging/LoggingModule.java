package com.oreilly.microservices.logging;

import com.google.inject.Scopes;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;

public class LoggingModule extends RequestScopeModule {

    @Override
    protected void configure() {
        bind(MdcFilter.class).in(Scopes.SINGLETON);
    }
}
