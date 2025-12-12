package com.example.application.port;

import com.example.domain.ChatMessage;

public interface ChatMessagePublisherPort {
    void publish(ChatMessage message);
}
