package com.oreilly.microservices.discovery;

import com.google.common.net.HostAndPort;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.async.ConsulResponseCallback;
import com.orbitz.consul.model.ConsulResponse;
import com.orbitz.consul.model.health.ServiceHealth;
import com.orbitz.consul.option.QueryOptions;

import javax.inject.Inject;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

import static com.orbitz.consul.option.QueryOptionsBuilder.builder;

/**
 * Service Discovery using a Consul agent.
 */
public class ConsulDiscovery implements ServiceDiscovery {
    private ConcurrentHashMap<String, List<HostAndPort>> services =
            new ConcurrentHashMap<>();

    private HealthClient healthClient;

    private Random random = new Random();

    /**
     * Constructor.
     *
     * @param consul A Consul client.
     */
    @Inject
    public ConsulDiscovery(Consul consul) {
        healthClient = consul.healthClient();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HostAndPort discover(String service) {
        List<HostAndPort> registrations = services.get(service);

        return registrations != null ?
                registrations.get(random.nextInt(registrations.size())) :
                doDiscover(service);
    }

    /**
     * Long polls Consul for changes to service registrations.
     *
     * @param service The name of the service to discover.
     * @return
     */
    private HostAndPort doDiscover(String service) {
        final ConsulResponse<List<ServiceHealth>> response = healthClient.getHealthyServiceInstances(service,
                QueryOptions.BLANK);
        AtomicReference<Long> index = new AtomicReference<>(response.getIndex());

        update(service, response);

        healthClient.getHealthyServiceInstances(service, QueryOptions.BLANK,
                new ConsulResponseCallback<List<ServiceHealth>>() {


                    @Override
                    public void onComplete(ConsulResponse<List<ServiceHealth>> consulResponse) {
                        update(service, consulResponse);
                        index.set(consulResponse.getIndex());

                        healthClient.getHealthyServiceInstances(service, builder()
                                .blockMinutes(1, index.get()).build(), this);
                    }

                    @Override
                    public void onFailure(Throwable throwable) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        healthClient.getHealthyServiceInstances(service, builder()
                                .blockMinutes(1, index.get()).build(), this);
                    }
                });

        return discover(service);
    }

    /**
     * Updates the internal map of service registrations with those specified in
     * the Consul response.
     *
     * @param service The service name.
     * @param response A {@link ConsulResponse} object containing the service registrations.
     */
    private void update(String service, ConsulResponse<List<ServiceHealth>> response) {
        services.put(service, response.getResponse().stream()
                .map((health) -> HostAndPort.fromParts(health.getService().getAddress(),
                        health.getService().getPort())).collect(Collectors.toList()));
    }
}
