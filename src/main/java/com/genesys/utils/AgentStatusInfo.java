package com.genesys.utils;

import java.util.List;

public record AgentStatusInfo(
        String agentId,
        String agentName,
        String status,           // "INTERACTING" / "AVAILABLE" / "OFF_QUEUE"
        List<String> queueNames
) {
}
