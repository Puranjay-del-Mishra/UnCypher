package com.UnCypher.models.dto;

import lombok.Data;
import java.time.Instant;

@Data
public class ChatMessage {
    private String messageId; // UUID or ULID
    private String userId;     // user UUID
    private String role;       // "user" or "assistant"
    private String content;    // actual message text
    private Instant timestamp; // when the message was generated
}
