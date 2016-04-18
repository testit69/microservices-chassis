package com.oreilly.microservices.metrics;

import com.google.inject.AbstractModule;
import com.google.inject.matcher.Matchers;
import io.prometheus.client.hotspot.DefaultExports;

public class MetricsModule extends AbstractModule {

    static {
        DefaultExports.initialize();
    }

    @Override
    protected void configure() {
        TimerInterceptor timerInterceptor = new TimerInterceptor();
        CounterInterceptor counterInterceptor = new CounterInterceptor();

        requestInjection(timerInterceptor);
        requestInjection(counterInterceptor);

        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Timed.class),
                timerInterceptor);
        bindInterceptor(Matchers.any(), Matchers.annotatedWith(Counted.class),
                counterInterceptor);
    }
}
