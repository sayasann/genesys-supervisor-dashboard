package com.genesys.service.component;

import com.genesys.factory.AnalyticsApiFactory;
import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.AnalyticsApi;
import com.mypurecloud.sdk.v2.model.*;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class GenesysQueueAggregatesClient {

    private final GenesysAuthService genesysAuthService;

    private final AnalyticsApiFactory analyticsApiFactory;

    public GenesysQueueAggregatesClient(
            GenesysAuthService genesysAuthService,
            AnalyticsApiFactory analyticsApiFactory
    ){
        this.genesysAuthService=genesysAuthService;
        this.analyticsApiFactory=analyticsApiFactory;
    }


    public ConversationAggregateQueryResponse fetchDailyAggregates(List<String> queueId) throws IOException, ApiException {

        AnalyticsApi analyticsApi = analyticsApiFactory.create(genesysAuthService.getValidClient());
        ConversationAggregationQuery query = buildAggregationQuery(queueId);
        return analyticsApi.postAnalyticsConversationsAggregatesQuery(query);
    }

    ConversationAggregationQuery buildAggregationQuery(List<String>queueId){
        ConversationAggregationQuery query = new ConversationAggregationQuery();
        query.setInterval(todayInterval());
        query.setMetrics(List.of(
                ConversationAggregationQuery.MetricsEnum.NOFFERED,
                ConversationAggregationQuery.MetricsEnum.TWAIT,
                ConversationAggregationQuery.MetricsEnum.THANDLE,
                ConversationAggregationQuery.MetricsEnum.TABANDON

        ));
        query.setGroupBy(List.of(ConversationAggregationQuery.GroupByEnum.QUEUEID));
        query.setFilter(buildFilter(queueId));
        return query;

    }

    private ConversationAggregateQueryFilter buildFilter(List<String> queueId){
        List<ConversationAggregateQueryPredicate> predicates = queueId.stream()
                .map(this::toPredicate)
                .toList();

        ConversationAggregateQueryFilter filter = new ConversationAggregateQueryFilter();
        filter.setType(ConversationAggregateQueryFilter.TypeEnum.OR);
        filter.setPredicates(predicates);
        return filter;


    }

    private ConversationAggregateQueryPredicate toPredicate(String queueId){
        ConversationAggregateQueryPredicate predicate = new ConversationAggregateQueryPredicate();
        predicate.setDimension(ConversationAggregateQueryPredicate.DimensionEnum.QUEUEID);
        predicate.setValue(queueId);
        return predicate;
    }

    private String todayInterval() {
        Instant now = Instant.now();
        Instant sevenDaysAgo = now.minus(7, ChronoUnit.DAYS); // geçici, sadece test için
        DateTimeFormatter fmt = DateTimeFormatter.ISO_INSTANT;
        return fmt.format(sevenDaysAgo) + "/" + fmt.format(now);
    }

}
