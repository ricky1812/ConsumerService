package com.example.consumerservice.controllers;

import com.example.consumerservice.models.Events;
import com.example.consumerservice.services.EventListenerService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.Arrays;
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
    public List<Events> findAll() {
        return eventListenerService.getAllEvents();
    }

    @GetMapping("/streams")
    public Flux<ServerSentEvent<Events>> streamEvents(@RequestParam(required = false) String type, @RequestParam(defaultValue = "5") int limit) {

        List<String> types = type==null?new ArrayList<>():Arrays.stream(type.split(",")).map(String::trim).filter(s -> !s.isEmpty()).toList();

        Flux<Events> pastevents = Flux.fromIterable(eventListenerService.getPastEvents(limit, types));


        Flux<Events> liveEvents = eventListenerService.streamEvents().filter(event -> type.isEmpty() || event.getType().equals(type));
        return Flux.concat(pastevents, liveEvents).map(events -> ServerSentEvent.builder(events).build());

    }


}
