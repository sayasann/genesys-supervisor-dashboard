package com.genesys.service.component;

import com.genesys.utils.QueueMetrics;
import com.genesys.utils.QueueObservationStats;
import com.mypurecloud.sdk.v2.model.ConversationAggregationQuery;
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


    public Map<String, QueueObservationStats> aggregate(QueueObservationQueryResponse response) {


        Map<String, QueueObservationStats> result = new HashMap<>();
        if (response == null || response.getResults().isEmpty()) {
            return result;
        }

        Map<String, List<QueueObservationDataContainer>> byQueueId = new HashMap<>();

        //her queueya ait interacting waiting tüm kanalları birleştiriyoruz(voice,chat, video etc.)
        for (var r : response.getResults()) {
            String queueId = r.getGroup().get("queueId");

            if (!byQueueId.containsKey(queueId)) {
                byQueueId.put(queueId, new ArrayList<>());
            }
            byQueueId.get(queueId).add(r);
        }

        for(Map.Entry<String, List<QueueObservationDataContainer>> entry: byQueueId.entrySet()){
            int waitingCalls = sumMetric(entry.getValue(),METRIC_WAITING);
            int talkingAgents = sumMetric(entry.getValue(),METRIC_INTERACTING);
            result.put(entry.getKey(),new QueueObservationStats(waitingCalls,talkingAgents));
        }
        return result;
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
