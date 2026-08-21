package com.genesys.service.component;

import com.genesys.utils.QueueAggregateStats;
import com.genesys.utils.QueueMetrics;
import com.genesys.utils.QueueObservationStats;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.AnalyticsApi;
import com.mypurecloud.sdk.v2.model.ConversationAggregateQueryResponse;
import com.mypurecloud.sdk.v2.model.ConversationAggregationQuery;
import com.mypurecloud.sdk.v2.model.Queue;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class QueueMetricsService {

    private final GenesysQueueDirectoryClient queueDirectoryClient;
    private final GenesysQueueMetricsClient queueMetricsClient;
    private final GenesysQueueAggregatesClient queueAggregatesClient;
    private final QueueObservationAggregator observationAggregator;
    private final ConversationAggregateAggregator conversationAggregator;

    public QueueMetricsService(GenesysQueueDirectoryClient queueDirectoryClient,
                               GenesysQueueMetricsClient queueMetricsClient,
                               QueueObservationAggregator aggregator,GenesysQueueAggregatesClient queueAggregatesClient,
                               ConversationAggregateAggregator conversationAggregator) {
        this.queueDirectoryClient = queueDirectoryClient;
        this.queueMetricsClient = queueMetricsClient;
        this.observationAggregator = aggregator;
        this.queueAggregatesClient = queueAggregatesClient;
        this.conversationAggregator=conversationAggregator;
    }


    public List<QueueMetrics> fetchQueueMetrics() throws IOException, ApiException {
        // 1. Division'daki tüm queue'ları çek (id + name)
        List<Queue> queueList =queueDirectoryClient.fetchQueues();



        // 2. id -> name eşleşmesi için bir Map kur, kolayca aranabilsin diye
        Map<String,String> queueNamesById = new HashMap<>();
        for(var v : queueList){
            queueNamesById.put(v.getId(),v.getName());
        }

        // 3. Observation query'yi çalıştır (id listesini filter olarak ver)
        List<String> queueIds = new ArrayList<>(queueNamesById.keySet());
        var observationResponse = queueMetricsClient.fetchQueueObservations(queueIds);
        // 4. Ham response'u queueId bazında topla (waitingCalls, talkingAgents)
        Map<String, QueueObservationStats> aggregated = observationAggregator.aggregate(observationResponse);

        // 3. ADIM: günlük toplamları çek (dailyTotalCalls, avgWaitSeconds, avgHandleSeconds, abandonRate)
        ConversationAggregateQueryResponse aggregateResponse = queueAggregatesClient.fetchDailyAggregates(queueIds);
        Map<String, QueueAggregateStats> aggregateStatsMap = conversationAggregator.aggregate(aggregateResponse);

        List<QueueMetrics> result = new ArrayList<>();

        for(String queueId: queueIds){
            String queueName = queueNamesById.get(queueId);

            QueueObservationStats observationStats = aggregated.get(queueId);
            if(observationStats == null) {
                observationStats = QueueObservationStats.empty();
            }

            QueueAggregateStats aggregateStats = aggregateStatsMap.get(queueId);
            if(aggregateStats==null){
                aggregateStats = ConversationAggregateAggregator.empty();
            }
            QueueMetrics metrics = new QueueMetrics(
                    queueId,queueName,observationStats.waitingCalls(), observationStats.talkingAgents(),
                    aggregateStats.totalCalls(), aggregateStats.avgWaitSeconds(),aggregateStats.avgHandleSeconds(),
                    aggregateStats.abandonRate()
            );
            result.add(metrics);
        }

        return result;
    }
}
