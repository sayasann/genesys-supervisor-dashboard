package com.genesys.utils;

public record QueueObservationStats(int waitingCalls, int talkingAgents) {

    public static QueueObservationStats empty(){
        return new QueueObservationStats(0,0);
    }
}
