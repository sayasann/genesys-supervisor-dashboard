package com.genesys.dto;

import com.genesys.utils.QueueMetrics;

import java.time.Instant;
import java.util.List;

public record QueueMetricsResponse(List<QueueMetrics> queueMetricsList,
                                   Instant lastSuccessfulFetch,
                                   boolean stale) {
}
