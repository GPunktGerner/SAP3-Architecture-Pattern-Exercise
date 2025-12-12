package com.example.client;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String bootstrapServers = "localhost:9092";
        String clientTopic = "client-to-server";
        String serverTopic = "server-to-client";
        String clientId = "client1";

        // Producer: Nachrichten an den Server senden
        Properties prodProps = new Properties();
        prodProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        prodProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prodProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        prodProps.put(ProducerConfig.CLIENT_ID_CONFIG, clientId);

        KafkaProducer<String, String> producer = new KafkaProducer<>(prodProps);

        // Consumer: Nachrichten vom Server empfangen
        Properties consProps = new Properties();
        consProps.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        consProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consProps.put(ConsumerConfig.GROUP_ID_CONFIG, clientId + "-group");
        consProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(consProps);
        consumer.subscribe(List.of(serverTopic));

        // Consumer-Thread
        Thread consumerThread = new Thread(() -> {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
                    for (ConsumerRecord<String, String> r : records) {
                        System.out.println("Server antwortet: " + r.value());
                    }
                }
            } catch (WakeupException e) {
                // Erwarten wir beim Stoppen
            } finally {
                consumer.close();
            }
        });

        consumerThread.start();

        // Main-Thread: Scanner
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if (line.equalsIgnoreCase("exit"))
                break;

            producer.send(new ProducerRecord<>(clientTopic, line), (metadata, exception) -> {
                if (exception != null)
                    System.out.println("Fehler beim Senden: " + exception);
                else
                    System.out.println("Nachricht gesendet, Offset: " + metadata.offset());
            });
            producer.flush(); // warten bis alle Nachrichten gesendet sind
        }

        // Beende Consumer-Thread sauber
        consumer.wakeup(); // weckt poll() auf und löst WakeupException aus
        consumerThread.join();
        producer.close();
        scanner.close();
        System.out.println("Client beendet.");
    }
}
