package com.example.infra.kafka;

import com.example.application.port.ChatMessagePublisherPort;
import com.example.domain.ChatMessage;
import org.apache.kafka.clients.producer.*;

import java.util.Map;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaChatPublisher implements ChatMessagePublisherPort {

    private final KafkaProducer<String, String> producer;
    private final String topic;

    public KafkaChatPublisher(String bootstrapServers, String topic) {
        this.topic = topic;
        producer = new KafkaProducer<>(Map.of(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class));
    }

    @Override
    public void publish(ChatMessage message) {
        producer.send(new ProducerRecord<>(topic, message.serialize()));
    }
}
