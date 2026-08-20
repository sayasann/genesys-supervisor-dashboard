package com.genesys.service.component;

import com.genesys.utils.QueueMetrics;
import com.mypurecloud.sdk.v2.ApiException;
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
    private final QueueObservationAggregator aggregator;

    public QueueMetricsService(GenesysQueueDirectoryClient queueDirectoryClient,
                               GenesysQueueMetricsClient queueMetricsClient,
                               QueueObservationAggregator aggregator) {
        this.queueDirectoryClient = queueDirectoryClient;
        this.queueMetricsClient = queueMetricsClient;
        this.aggregator = aggregator;
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
        List<QueueMetrics> aggregated = aggregator.aggregate(observationResponse);

        // 5. Her QueueMetrics'e, Map'ten bulduğumuz queueName'i işleyerek yeni bir liste kur
        List<QueueMetrics> enriched = new ArrayList<>();
        for(QueueMetrics metrics:aggregated){
            String queueName = queueNamesById.get(metrics.queueId());

            QueueMetrics withName = new QueueMetrics(
                    metrics.queueId(),
                    queueName,
                    metrics.waitingCalls(),
                    metrics.talkingAgents(),
                    metrics.dailyTotalCalls(),
                    metrics.avgWaitSeconds(),
                    metrics.avgHandleSeconds(),
                    metrics.abandonRate()
            );
            enriched.add(withName);
        }

        return enriched;
    }
}
