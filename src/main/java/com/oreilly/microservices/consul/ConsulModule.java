package com.oreilly.microservices.consul;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Scopes;
import com.google.inject.Singleton;
import com.orbitz.consul.Consul;
import com.oreilly.microservices.discovery.ClientFactory;
import com.oreilly.microservices.discovery.ConsulDiscovery;
import com.oreilly.microservices.discovery.ServiceDiscovery;
import com.typesafe.config.Config;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;

import javax.ws.rs.client.ClientBuilder;

public class ConsulModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(ObjectMapper.class).in(Scopes.SINGLETON);
        bind(ConsulRegistrator.class).in(Scopes.SINGLETON);
        bind(ConsulLifecycleListener.class).in(Scopes.SINGLETON);
        bind(ServiceDiscovery.class).to(ConsulDiscovery.class).in(Scopes.SINGLETON);
        bind(ClientFactory.class).in(Scopes.SINGLETON);
    }

    @Provides
    @Singleton
    public ClientBuilder clientBuilder(ObjectMapper objectMapper) {
        ResteasyClientBuilder builder = new ResteasyClientBuilder();

        builder.connectionPoolSize(20);
        builder.register(new JacksonJaxbJsonProvider(objectMapper,
                JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS), 100000);

        return builder;
    }

    @Provides
    @Singleton
    public Consul consul(Config config, ClientBuilder clientBuilder) {
//    	System.setProperty("discovery.consul.host", "192.168.33.10");
//    	System.setProperty("discovery.consul.port", "8500");
        String host = config.getString("discovery.consul.host");
        int port = config.getInt("discovery.consul.port");

        return Consul.newClient(host, port, clientBuilder);
    }
}
