package com.UnCypher.services;

import com.UnCypher.models.dto.InsightAgentResponse;
import com.UnCypher.models.dto.InsightResponse;
import com.UnCypher.models.dto.MapCommand;
import com.UnCypher.models.dto.MapCommandBatch;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class InsightResponseProcessor {

    private final CommandDispatcher commandDispatcher;

    /**
     * Process the agent response and optionally dispatch map commands.
     */
    public InsightResponse processAgentResponse(InsightAgentResponse agentResponse) {

        InsightResponse response = new InsightResponse();
        response.setUserId(agentResponse.getUserId());
        response.setQuery(agentResponse.getQuery());
        response.setAnswer(agentResponse.getChatResponse());
        response.setIntents(agentResponse.getIntents());

        // ✅ Use mapCommandBatch directly
        MapCommandBatch batch = agentResponse.getMapCommandBatch();

        if (batch != null && batch.getCommands() != null && !batch.getCommands().isEmpty()) {
            List<MapCommand> commands = batch.getCommands();

            if (commands.size() == 1) {
                // Single command → dispatch individually
                commandDispatcher.dispatch(agentResponse.getUserId(), commands.get(0));
            } else {
                // Batch → make sure userId is set
                batch.setUserId(agentResponse.getUserId());
                commandDispatcher.dispatchBatch(agentResponse.getUserId(), batch);
            }
        }

        return response;
    }

    /**
     * Process incoming MapCommandBatch (used by /mapcommand route).
     */
    public void processMapCommands(String userId, List<MapCommand> commands) {
        if (commands == null || commands.isEmpty()) return;

        MapCommandBatch batch = new MapCommandBatch();
        batch.setUserId(userId);
        batch.setCommands(commands);

        commandDispatcher.dispatchBatch(userId, batch);
    }
}


