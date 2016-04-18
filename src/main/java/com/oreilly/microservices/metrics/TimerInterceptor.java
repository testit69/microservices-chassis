package com.oreilly.microservices.metrics;

import io.prometheus.client.Histogram;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.concurrent.ConcurrentHashMap;

public class TimerInterceptor implements MethodInterceptor {

    private ConcurrentHashMap<String, Histogram> registered = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Timed timed = invocation.getStaticPart().getAnnotation(Timed.class);
        String name = timed.name();
        Histogram histogram = registered.computeIfAbsent(name,
                s -> Histogram.build().name(name).help(timed.help()).register());
        Histogram.Timer timer = histogram.startTimer();

        try {
            return invocation.proceed();
        } finally {
            timer.observeDuration();
        }
    }
}
