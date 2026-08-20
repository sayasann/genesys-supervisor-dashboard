package com.genesys.service.component;

import com.genesys.factory.AnalyticsApiFactory;
import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.AnalyticsApi;
import com.mypurecloud.sdk.v2.model.QueueObservationQuery;
import com.mypurecloud.sdk.v2.model.QueueObservationQueryFilter;
import com.mypurecloud.sdk.v2.model.QueueObservationQueryPredicate;
import com.mypurecloud.sdk.v2.model.QueueObservationQueryResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
public class GenesysQueueMetricsClient {

    private final GenesysAuthService genesysAuthService;
    private final AnalyticsApiFactory analyticsApiFactory;

    public GenesysQueueMetricsClient(GenesysAuthService genesysAuthService,
                                     AnalyticsApiFactory analyticsApiFactory){
        this.genesysAuthService = genesysAuthService;
        this.analyticsApiFactory = analyticsApiFactory;
    }


    public QueueObservationQueryResponse fetchQueueObservations(List<String> queueIds) throws IOException, ApiException {
        AnalyticsApi analyticsApi = analyticsApiFactory.create(genesysAuthService.getValidClient());
        QueueObservationQuery query = buildQueueObservationQuery(queueIds);

        return analyticsApi.postAnalyticsQueuesObservationsQuery(query);
    }

    QueueObservationQuery buildQueueObservationQuery(List<String> queueIds){
        QueueObservationQuery query = new QueueObservationQuery();
        query.setMetrics(List.of(
                QueueObservationQuery.MetricsEnum.OWAITING,
                QueueObservationQuery.MetricsEnum.OINTERACTING
        ));
        query.setFilter(buildFilter(queueIds));
        return query;
    }
    QueueObservationQueryFilter buildFilter(List<String> queueIds) {
        List<QueueObservationQueryPredicate> predicates = queueIds.stream()
                .map(this::toPredicate)
                .toList();

        QueueObservationQueryFilter filter = new QueueObservationQueryFilter();
        filter.setType(QueueObservationQueryFilter.TypeEnum.OR);
        filter.setPredicates(predicates);
        return filter;
    }
    private QueueObservationQueryPredicate toPredicate(String queueId) {
        QueueObservationQueryPredicate predicate = new QueueObservationQueryPredicate();
        predicate.setDimension(QueueObservationQueryPredicate.DimensionEnum.QUEUEID);
        predicate.setValue(queueId);
        return predicate;
    }
}
