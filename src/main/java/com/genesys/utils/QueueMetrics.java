package com.genesys.utils;

public record QueueMetrics(String queueId, String queueName, int waitingCalls,
                           int talkingAgents, int dailyTotalCalls,
                           long avgWaitSeconds, long avgHandleSeconds, double abandonRate) {
}
