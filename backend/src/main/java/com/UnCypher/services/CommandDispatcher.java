package com.UnCypher.services;

import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.models.dto.MapCommandBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CommandDispatcher {

    private final SimpMessagingTemplate messagingTemplate;

    public void dispatch(String userId, MapCommand command) {
        messagingTemplate.convertAndSend(
                "/topic/mapCommand/" + userId,
                command
        );
    }

    public void dispatchBatch(String userId, MapCommandBatch batch) {
        messagingTemplate.convertAndSend(
                "/topic/mapCommandBatch/" + userId,
                batch
        );
    }
}



