package com.example.consumerservice.controllers;

import com.example.consumerservice.models.Events;
import com.example.consumerservice.services.EventListenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/events")
public class EventsController {
    private EventListenerService eventListenerService;
    @Autowired
    private ObjectMapper objectMapper;
    public EventsController(EventListenerService eventListenerService) {
        this.eventListenerService = eventListenerService;
    }
    @GetMapping
    public List<Events> findAll(){
        return eventListenerService.getAllEvents();
    }
//    @GetMapping(value = "/streams", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
//    public Flux<String> streamEvents() {
//        return eventListenerService.streamEvents()
//                .map(event -> "data:" + toJson(event) + "\n\n");
//    }
@GetMapping("/streams")
public Flux<ServerSentEvent<Events>> streamEvents() {
    return eventListenerService.streamEvents()
            .map(event -> ServerSentEvent.builder(event).build());
}

    private String toJson(Events event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
