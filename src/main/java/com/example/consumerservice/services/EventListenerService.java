package com.example.consumerservice.services;

import com.example.consumerservice.models.Events;
import com.example.consumerservice.repositories.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.List;

@Service
public class EventListenerService {
    private EventRepository eventRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Sinks.Many<Events> sink;

    public EventListenerService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @KafkaListener(topics = "eventhub", groupId = "eventhub-group")
    public void consumeMessage(String message) {
        System.out.println("📩 Received event: " + message);
        try {
            JsonNode jsonNode = objectMapper.readTree(message);
            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "unknown";

            Events event = new Events(type, message);
            eventRepository.save(event);
            sink.tryEmitNext(event);

            System.out.println("💾 Saved event: " + type);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }


    }

    public Flux<Events> streamEvents() {
        return sink.asFlux();
    }

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }
}