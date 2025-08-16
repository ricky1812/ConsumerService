package com.example.consumerservice.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DLQService {
    @Autowired
    private ConsumerFactory<String,String> consumerFactory;
    public List<String> getDlqMessages(int maxMessages) {
        List<String> messages = new ArrayList<>();

        ContainerProperties containerProps = new ContainerProperties("eventhub-dlq");
        KafkaMessageListenerContainer<String, String> container =
                new KafkaMessageListenerContainer<>(consumerFactory, containerProps);

        container.setupMessageListener((MessageListener<String, String>) record -> {
            if (messages.size() < maxMessages) {
                messages.add(record.value());
            }
        });
        container.start();

        try {
            Thread.sleep(2000); // small wait to consume messages
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } finally {
            container.stop();
        }
        return messages;
    }
}
