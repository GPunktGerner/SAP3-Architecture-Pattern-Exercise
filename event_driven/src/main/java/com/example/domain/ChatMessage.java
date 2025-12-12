package com.example.domain;

public class ChatMessage {
    public final String content;

    public ChatMessage(String content) {
        this.content = content;
    }

    public String serialize() {
        return content; // nur String, keine JSON
    }

    public static ChatMessage deserialize(String s) {
        return new ChatMessage(s);
    }
}
