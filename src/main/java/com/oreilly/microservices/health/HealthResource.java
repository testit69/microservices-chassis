package com.oreilly.microservices.health;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheckRegistry;
import com.google.inject.Inject;

import javax.inject.Provider;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import java.util.Map;

@Path("health")
public class HealthResource {

    @Inject
    private Provider<HealthCheckRegistry> healthCheckRegistryProvider;

    @GET
    @Produces("application/json")
    public Response health() {
        Map<String, HealthCheck.Result> result =
                healthCheckRegistryProvider.get().runHealthChecks();
        boolean healthy = result.values().stream()
                .allMatch((checkResult) -> checkResult.isHealthy());

        return Response.status(healthy ?
                Response.Status.OK :
                Response.Status.SERVICE_UNAVAILABLE).entity(result).build();
    }
}

