package com.example.consumerservice.controllers;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class EventListenerService {

    @KafkaListener(topics = "eventhub", groupId = "eventhub-group")
    public void consumeMessage(String message) {
        System.out.println("📩 Received event: " + message);
    }
}