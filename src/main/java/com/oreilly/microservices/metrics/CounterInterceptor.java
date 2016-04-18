package com.oreilly.microservices.metrics;

import io.prometheus.client.Counter;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CounterInterceptor implements MethodInterceptor {

    private Map<String, Counter> registered = new ConcurrentHashMap<>();

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Counted counted = invocation.getStaticPart().getAnnotation(Counted.class);
        String name = counted.name();
        Counter counter = registered.computeIfAbsent(name,
                s -> Counter.build().name(name).help(counted.help()).register());

        try {
            return invocation.proceed();
        } finally {
            counter.inc();
        }
    }

}
