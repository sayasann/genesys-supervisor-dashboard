package com.genesys.service.component;

import com.genesys.utils.QueueMetrics;
import com.mypurecloud.sdk.v2.model.QueueObservationDataContainer;
import com.mypurecloud.sdk.v2.model.QueueObservationQuery;
import com.mypurecloud.sdk.v2.model.QueueObservationQueryResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class QueueObservationAggregator {

    private static final String METRIC_WAITING = "oWaiting";
    private static final String METRIC_INTERACTING = "oInteracting";


    public List<QueueMetrics> aggregate(QueueObservationQueryResponse response) {

        List<QueueMetrics> output = new ArrayList<>();

        if (response == null || response.getResults().isEmpty()) {
            return List.of();
        }

        Map<String, List<QueueObservationDataContainer>> byQueueId = new HashMap<>();

        //her queueya ait interacting waiting tüm kanalları birleştiriyoruz(voice,chat, video etc.)
        for (var result : response.getResults()) {
            String queueId = result.getGroup().get("queueId");

            if (!byQueueId.containsKey(queueId)) {
                byQueueId.put(queueId, new ArrayList<>());
            }
            byQueueId.get(queueId).add(result);
        }

        for (Map.Entry<String, List<QueueObservationDataContainer>> entry : byQueueId.entrySet()) {
            String queueId = entry.getKey();
            List<QueueObservationDataContainer> resultsForQueue = entry.getValue();

            int waitingCalls =sumMetric(resultsForQueue,METRIC_WAITING);
            int talkingAgents = sumMetric(resultsForQueue,METRIC_INTERACTING);
            QueueMetrics metrics = new QueueMetrics(queueId,null,waitingCalls,
                    talkingAgents,0,0,0L,0L,0.0);
            output.add(metrics);
        }
        return output;


    }

    private int sumMetric(List<QueueObservationDataContainer> list, String metricName){

        int total =0;

        for(var result : list){

            if(result.getData()==null){
                continue;
            }

            for(var dataItem : result.getData()){
                if(!metricName.equals(dataItem.getMetric())){
                    continue;
                }
                if(dataItem.getStats()!=null && dataItem.getStats().getCount()!=null){
                    total+=dataItem.getStats().getCount();
                }
            }

        }
        return total;
    }
}
