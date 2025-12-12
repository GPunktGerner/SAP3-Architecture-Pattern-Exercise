package com.example;

import com.example.application.ChatService;
import com.example.infra.kafka.KafkaChatConsumer;
import com.example.infra.kafka.KafkaChatPublisher;

public class Main {
    public static void main(String[] args) {
        String bootstrapServers = "localhost:9092";
        String clientTopic = "client-to-server";
        String serverTopic = "server-to-client";

        // Publisher: Server schreibt auf serverTopic
        KafkaChatPublisher serverPublisher = new KafkaChatPublisher(bootstrapServers, serverTopic);

        // Service
        ChatService service = new ChatService(serverPublisher);

        // Consumer: Server liest vom clientTopic
        new KafkaChatConsumer(bootstrapServers, clientTopic, "server-group", service);

        System.out.println("Server läuft. Wartet auf Nachrichten auf Topic: " + clientTopic);
    }
}
