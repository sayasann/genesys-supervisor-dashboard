package com.genesys.utils;


import java.time.Instant;
import java.util.List;

public record CachedAgentStatus(
        List<AgentStatusInfo> agents,
        int interactingCount,
        int availableCount,
        int offQueueCount,
        Instant lastSuccessfulFetch,
        boolean stale
) {
    public static CachedAgentStatus empty(){
        return new CachedAgentStatus(
                List.of(),0,0,0,Instant.EPOCH,true
        );
    }
}
