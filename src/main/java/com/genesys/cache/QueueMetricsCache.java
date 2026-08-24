package com.genesys.cache;

import com.genesys.utils.CachedQueueMetrics;
import com.genesys.utils.QueueMetrics;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class QueueMetricsCache {


    private final AtomicReference<CachedQueueMetrics> ref =
            new AtomicReference<>(CachedQueueMetrics.empty());

    public void updateWithFreshData(List<QueueMetrics> queueMetricsList){
        ref.set(new CachedQueueMetrics(queueMetricsList, Instant.now(),false));
    }

    public void markStale(){
        CachedQueueMetrics current = ref.get();
        if(current.queues().isEmpty()){
            return;
        }
        ref.set(new CachedQueueMetrics(current.queues(),current.lastSuccessfulFetch(),true));
    }

    public CachedQueueMetrics get(){
        return ref.get();
    }
}
