package com.oreilly.microservices.discovery;

import com.google.common.net.HostAndPort;
import retrofit.Endpoint;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * Retrofit {@link retrofit.Endpoint} that supports service discovery
 * mechanisms.
 */
public class DiscoverableEndpoint implements Endpoint {

    private String serviceName;
    private ServiceDiscovery serviceDiscovery;

    /**
     * Constructs an instance of this class.
     *
     * @param serviceName The service name.
     * @param serviceDiscovery The service discovery type.
     */
    public DiscoverableEndpoint(String serviceName, ServiceDiscovery serviceDiscovery) {
        this.serviceName = serviceName;
        this.serviceDiscovery = serviceDiscovery;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getUrl() {
        HostAndPort hostAndPort = serviceDiscovery.discover(serviceName);

        try {
            return new URL("http", hostAndPort.getHostText(), hostAndPort.getPort(), "").toExternalForm();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return serviceName;
    }
}
