package com.genesys.utils;

public record QueueAggregateStats(
        int totalCalls,
        long avgWaitSeconds,
        long avgHandleSeconds,
        double abandonRate
) {
}
