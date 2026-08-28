package com.genesys.CacheTest;

import com.genesys.cache.QueueMetricsCache;
import com.genesys.utils.CachedQueueMetrics;
import com.genesys.utils.QueueMetrics;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

public class QueueMetricsCacheTest {

    private final QueueMetricsCache cache = new QueueMetricsCache();

    @Test
    void cache_is_stale_at_first() {
        CachedQueueMetrics result = cache.get();

        assertThat(result.queues()).isEmpty();
        assertThat(result.stale()).isTrue();
    }

    @Test
    void check_stale() {
        QueueMetrics dummy = new QueueMetrics("q1", "Test Queue", 3, 2, 10, 45, 60, 0.1);
        cache.updateWithFreshData(List.of(dummy));

        Instant befıreFetch = cache.get().lastSuccessfulFetch();

        cache.markStale();
        CachedQueueMetrics result = cache.get();

        // ASIL KRİTİK KONTROL: veri hâlâ orada mı
        assertThat(result.queues()).hasSize(1);
        assertThat(result.queues().get(0).queueId()).isEqualTo("q1");
        assertThat(result.stale()).isTrue();
        // zaman damgası DEĞİŞMEMİŞ olmalı — eski veriye "yeni" gibi davranmıyoruz
        assertThat(result.lastSuccessfulFetch()).isEqualTo(befıreFetch);
    }

    @Test
    void stale_is_false_after_new_data() {
        QueueMetrics dummy = new QueueMetrics("q1", "Test Queue", 3, 2, 10, 45, 60, 0.1);
        cache.updateWithFreshData(List.of(dummy));
        cache.markStale();

        assertThat(cache.get().stale()).isTrue();

        QueueMetrics newDummy = new QueueMetrics("q2", "Yeni Queue", 5, 1, 8, 30, 50, 0.05);
        cache.updateWithFreshData(List.of(newDummy));

        CachedQueueMetrics result = cache.get();
        assertThat(result.stale()).isFalse();
        assertThat(result.queues()).hasSize(1);
        assertThat(result.queues().get(0).queueId()).isEqualTo("q2");
    }


}
