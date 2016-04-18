package com.oreilly.microservices.discovery;

import com.google.inject.Inject;
import com.oreilly.microservices.logging.MdcFilter;
import org.slf4j.MDC;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;

public class ClientFactory {

    @Inject
    private ServiceDiscovery serviceDiscovery;

    @Inject
    public ClientFactory(ServiceDiscovery serviceDiscovery) {
        this.serviceDiscovery = serviceDiscovery;
    }

    public <T> T buildClient(String service, Class<T> klazz) {
        return new RestAdapter.Builder()
                .setEndpoint(new DiscoverableEndpoint(service, serviceDiscovery))
                .setConverter(new JacksonConverter())
                .setClient(new OkClient())
                .setRequestInterceptor(request -> {
                    if(MDC.get(MdcFilter.MDC_SESSION_ID) != null) {
                        request.addHeader(MdcFilter.HEADER_SESSION_ID,
                                MDC.get(MdcFilter.MDC_SESSION_ID));
                    }
                })
                .build().create(klazz);
    }
}
