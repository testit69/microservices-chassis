package com.oreilly.microservices;

import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.google.inject.servlet.ServletModule;
import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;
import com.oreilly.microservices.config.ConfigModule;
import com.oreilly.microservices.consul.ConsulModule;
import com.oreilly.microservices.health.HealthModule;
import com.oreilly.microservices.health.checks.ChecksModule;
import com.oreilly.microservices.logging.LoggingModule;
import com.oreilly.microservices.logging.MdcFilter;
import com.oreilly.microservices.metrics.MetricsModule;
import io.prometheus.client.exporter.MetricsServlet;
import org.jboss.resteasy.plugins.guice.GuiceResteasyBootstrapServletContextListener;
import org.jboss.resteasy.plugins.guice.ext.RequestScopeModule;
import org.jboss.resteasy.plugins.providers.jackson.ResteasyJackson2Provider;
import org.jboss.resteasy.plugins.server.servlet.HttpServletDispatcher;

public class ChassisModule extends RequestScopeModule {

    @Override
    protected void configure() {
        bind(GuiceResteasyBootstrapServletContextListener.class);

        install(new ConfigModule());
        install(new HealthModule());
        install(new ChecksModule());
        install(new ConsulModule());
        install(new LoggingModule());
        install(new MetricsModule());

        install(new ServletModule() {
            @Override
            protected void configureServlets() {
                filter("/*").through(MdcFilter.class);
                bind(HystrixMetricsStreamServlet.class).in(Scopes.SINGLETON);
                bind(HttpServletDispatcher.class).in(Scopes.SINGLETON);
                bind(MetricsServlet.class).in(Scopes.SINGLETON);
                serve("/metrics").with(MetricsServlet.class);
                serve("/hystrix.stream").with(HystrixMetricsStreamServlet.class);
                serve("/*").with(HttpServletDispatcher.class);
            }
        });
    }

    @Provides
    @Singleton
    ResteasyJackson2Provider getJacksonJsonProvider() {
        return new ResteasyJackson2Provider();
    }
}
