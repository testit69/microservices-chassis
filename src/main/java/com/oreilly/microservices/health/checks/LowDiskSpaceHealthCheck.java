package com.oreilly.microservices.health.checks;

import com.codahale.metrics.health.HealthCheck;

import java.io.File;

public class LowDiskSpaceHealthCheck extends HealthCheck {

    private static final int THRESHOLD = 10 * 1024 * 1024;

    @Override
    protected Result check() throws Exception {
        File file = new File(".");
        long free = file.getFreeSpace();

        if(free < THRESHOLD) {
            return Result.unhealthy("Low disk space");
        } else {
            return Result.healthy(String.format("%d bytes free", free));
        }
    }
}
