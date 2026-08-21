package com.genesys.service.component;

import com.genesys.utils.QueueAggregateStats;
import com.mypurecloud.sdk.v2.model.ConversationAggregateDataContainer;
import com.mypurecloud.sdk.v2.model.ConversationAggregateQueryResponse;
import com.mypurecloud.sdk.v2.model.StatisticalResponse;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ConversationAggregateAggregator {

    private static final String METRIC_OFFERED = "nOffered";
    private static final String METRIC_WAIT = "tWait";
    private static final String METRIC_HANDLE = "tHandle";
    private static final String METRIC_ABANDON = "tAbandon";

    public static QueueAggregateStats empty(){
        return new QueueAggregateStats(0,0L,0L,0.0);

    }

    public Map<String,QueueAggregateStats> aggregate(ConversationAggregateQueryResponse response){
        Map<String, QueueAggregateStats> result = new HashMap<>();

        if(response.getResults()==null){
            return result;
        }

        Map<String, List<ConversationAggregateDataContainer>> byQueueId = new HashMap<>();

        //her data+group containerini medityeları tek bir listede topladım ve o queueidye ait oldu
        for(ConversationAggregateDataContainer r : response.getResults()){
            String queueId = r.getGroup().get("queueId");

            if(!byQueueId.containsKey(queueId)){
                byQueueId.put(queueId,new ArrayList<>());
            }
            byQueueId.get(queueId).add(r);
        }

        for(Map.Entry<String,List<ConversationAggregateDataContainer>> entry: byQueueId.entrySet()){
            String queueId = entry.getKey();
            List<ConversationAggregateDataContainer> rows = entry.getValue();

            long offeredCount = sumCount(rows,METRIC_OFFERED);
            long abandonCount = sumCount(rows,METRIC_ABANDON);

            long waitSum = sumStat(rows, METRIC_WAIT,true);
            long waitCount = sumStat(rows, METRIC_WAIT, false);
            long handleSum = sumStat(rows, METRIC_HANDLE, true);
            long handleCount = sumStat(rows, METRIC_HANDLE, false);


            long avgWaitMs=0;
            if(waitCount!=0){
                avgWaitMs =  waitSum/waitCount;
            }

            long avgHandleMs = 0;
            if(handleCount!=0){
                avgHandleMs=handleSum/handleCount;
            }

            double abandonRate = 0.0;

            if(offeredCount!=0){
                abandonRate = (double) abandonCount /offeredCount;
            }
            QueueAggregateStats stats = new QueueAggregateStats(
                    (int) offeredCount,
                    avgWaitMs/1000, //ms
                    avgHandleMs/1000,
                    abandonRate
            );
            result.put(queueId,stats);



        }
        return result;


    }

    // Bir metric'in TÜM mediaType satırlarındaki count'unu toplar (nOffered, tAbandon için)
    private long sumCount(List<ConversationAggregateDataContainer> rows,String metricName){

        long total = 0;
        for(ConversationAggregateDataContainer row: rows){
            for(var dataContainer: row.getData()){

                if(dataContainer.getMetrics() == null){
                    continue;
                }

                for(var metric : dataContainer.getMetrics()){
                    boolean isRightMetric = metricName.equals(metric.getMetric());
                    boolean hasStats = metric.getStats()!=null && metric.getStats().getCount()!=null;

                    if(isRightMetric && hasStats) total+=metric.getStats().getCount();


                }




            }
        }
        return total;

    }

    private long sumStat(List<ConversationAggregateDataContainer> rows, String metricName, boolean wantSum){
        long total = 0;

        for(ConversationAggregateDataContainer row : rows){
            for(var dataContainer: row.getData()){
                if(dataContainer.getMetrics()==null) continue;

                for(var metric: dataContainer.getMetrics()){
                    if(!metricName.equals(metric.getMetric()) || metric.getStats()==null){
                        continue;
                    }
                    Long value;
                    if(wantSum){
                        value = toLong(metric.getStats().getSum());
                    }else{
                        value=metric.getStats().getCount();
                    }
                    if(value!=null) {
                        total+=value;
                    }
                }
            }
        }
        return total;

    }
    private Long toLong(BigDecimal value) {
        if (value == null) {
            return null;
        }
        return value.longValue();
    }

}
