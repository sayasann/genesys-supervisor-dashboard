package com.genesys.schedulers;

import com.genesys.cache.QueueMetricsCache;
import com.genesys.service.QueueMetricsService;
import com.genesys.utils.QueueMetrics;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor // create constructor for globals and autowire
@Slf4j
public class QueueMetricsScheduler {

    private final QueueMetricsService queueMetricsService;
    private final QueueMetricsCache cache;

    @Scheduled(fixedRate = 15000) //15 sec
    public void refreshQueueMetrics(){

        try {
            List<QueueMetrics> fresh = queueMetricsService.fetchQueueMetrics();
            cache.updateWithFreshData(fresh);
        }catch (Exception e){ //catches all exceptions + base exceptions (getValidClint() GENESYS_AUTH_FAILED)
            log.warn("Queue metrics couldn't be updated, its marked as stale! {}",e.getMessage());
            cache.markStale();
        }
    }
}
