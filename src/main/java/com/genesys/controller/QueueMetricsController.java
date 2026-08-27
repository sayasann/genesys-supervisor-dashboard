package com.genesys.controller;

import com.genesys.cache.QueueMetricsCache;
import com.genesys.utils.CachedQueueMetrics;
import com.genesys.utils.QueueMetrics;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class QueueMetricsController {

    private final QueueMetricsCache cache;

    public QueueMetricsController(QueueMetricsCache cache){
        this.cache=cache;
    }

    @PreAuthorize("hasRole('SUPERVISOR') or hasRole('ADMIN')")
    @GetMapping("/queues/metrics")
    public ResponseEntity<CachedQueueMetrics> getQueueMetrics(){
        CachedQueueMetrics cached = cache.get();
        //if cache is empty, can happen only at the start
        if(cached.queues().isEmpty()){
            return ResponseEntity.status(502).body(cached);
        }

        return ResponseEntity.ok(cached);
    }
}
