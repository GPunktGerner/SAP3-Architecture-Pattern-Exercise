package com.example.infra.kafka;

import com.example.application.ChatService;
import com.example.domain.ChatMessage;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;

public class KafkaChatConsumer {

    private final KafkaConsumer<String, String> consumer;
    private final ChatService service;

    public KafkaChatConsumer(String bootstrapServers, String topic, String groupId, ChatService service) {
        this.service = service;
        Properties props = new Properties();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.put(ConsumerConfig.GROUP_ID_CONFIG, groupId);
        props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        consumer = new KafkaConsumer<>(props);
        consumer.subscribe(List.of(topic));
        new Thread(this::pollLoop).start();
    }

    private void pollLoop() {
        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> r : records) {
                ChatMessage msg = ChatMessage.deserialize(r.value());
                service.processMessage(msg);
            }
        }
    }
}
