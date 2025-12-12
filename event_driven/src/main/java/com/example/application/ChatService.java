package com.example.application;

import com.example.application.port.ChatMessagePublisherPort;
import com.example.domain.ChatMessage;

public class ChatService {

    private final ChatMessagePublisherPort publisher;

    public ChatService(ChatMessagePublisherPort publisher) {
        this.publisher = publisher;
    }

    // verarbeitet Nachricht vom Client
    public void processMessage(ChatMessage input) {
        String newContent = input.content + " [vom Server bearbeitet]";
        ChatMessage output = new ChatMessage(newContent);
        publisher.publish(output);
    }
}
