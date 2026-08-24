package com.genesys.utils;

import java.time.Instant;
import java.util.List;

public record CachedQueueMetrics(
        List<QueueMetrics> queues,
        Instant lastSuccessfulFetch,
        boolean stale
) {

    public static CachedQueueMetrics empty(){
        return new CachedQueueMetrics(List.of(),Instant.EPOCH,true);
    }
}
