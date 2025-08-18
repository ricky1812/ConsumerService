package com.example.consumerservice.services;

import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Service;

@Service
public class MetricsService {
    private final MeterRegistry registry;

    public MetricsService(MeterRegistry registry) {
        this.registry = registry;
    }

    public void incrementEventCounter(String type) {
        registry.counter("eventhub_events_total", "type", type).increment();
    }
}