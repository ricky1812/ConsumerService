package com.example.consumerservice.services;

import com.example.consumerservice.models.Events;
import com.example.consumerservice.repositories.EventRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class EventListenerService {
    private EventRepository eventRepository;
    private ObjectMapper objectMapper = new ObjectMapper();
    private final Sinks.Many<Events> sink;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public EventListenerService(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
        this.sink = Sinks.many().multicast().onBackpressureBuffer();
    }

    @KafkaListener(topics = "eventhub", groupId = "eventhub-group")
    public void consumeMessage(ConsumerRecord<String, String> record, Acknowledgment ack) {
        log.info("Consumed key={}, partition={}, offset={}, value={}",
                record.key(), record.partition(), record.offset(), record.value());

        try {
            String message = record.value();
            log.info("Received event: {}",message);
            JsonNode jsonNode = objectMapper.readTree(message);
            String eventId = jsonNode.has("id") ? jsonNode.get("id").asText() : UUID.randomUUID().toString();

            String type = jsonNode.has("type") ? jsonNode.get("type").asText() : "unknown";
            if (eventRepository.existsByEventID(eventId)) {
                log.info("Duplicate event detected: {}", eventId);
                ack.acknowledge();
                return;
            }
            Events event = new Events(type, message,eventId);
            eventRepository.save(event);
            sink.tryEmitNext(event);

            System.out.println("💾 Saved event: " + event);
        } catch (Exception e) {
            log.error("Failed to process event, sending to DLQ", e);
            kafkaTemplate.send("eventhub-dlq", record.value());
            ack.acknowledge();

        }



    }

    public Flux<Events> streamEvents() {
        return sink.asFlux();
    }

    public List<Events> getAllEvents() {
        return eventRepository.findAll();
    }


    public List<Events> getAllEventsByOrder(int limit) {
        return eventRepository.findTopNByOrderByCreatedAtDesc(limit);
    }

    public List<Events> getPastEvents(int limit, List<String> types) {
        if (types == null || types.isEmpty()) {
            return eventRepository.findTopNByOrderByCreatedAtDesc(limit);
        }
        return eventRepository.findTopNByTypeInOrderByCreatedAtDesc(types, limit);


    }
}