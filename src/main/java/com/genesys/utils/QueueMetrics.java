package com.genesys.utils;

public record QueueMetrics(String queueId, String queueName, int waitingCalls,
                           int talkingAgents, int totalAgents, int dailyTotalCalls,
                           long avgWaitSeconds, long avgHandleSeconds, double abandonRate) {
}
