package com.genesys.controller;

import com.genesys.service.GenesysAuthService;
import com.genesys.service.component.*;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.model.Queue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

@RestController
public class TestController {


    private final GenesysAuthService genesysAuthService;
    private final GenesysQueueMetricsClient client;
    private final GenesysQueueDirectoryClient user;
    private final QueueObservationAggregator aggregator;
    private final QueueMetricsService queueMetricsService;
    private final GenesysQueueAggregatesClient aggregator2;
    private final ConversationAggregateAggregator aggregator3;

    public TestController(GenesysAuthService genesysAuthService,GenesysQueueMetricsClient client,
                          GenesysQueueDirectoryClient user,QueueObservationAggregator aggregator,
                          QueueMetricsService queueMetricsService, GenesysQueueAggregatesClient aggregator2,
                          ConversationAggregateAggregator conversationAggregateAggregator){
        this.genesysAuthService =  genesysAuthService;
        this.client=client;
        this.user=user;
        this.aggregator=aggregator;
        this.queueMetricsService = queueMetricsService;
        this.aggregator2=aggregator2;
        this.aggregator3=conversationAggregateAggregator;
    }


    @GetMapping("/test/genesys-ping")
    public Object ping() throws ApiException, IOException{

        try {

            return queueMetricsService.fetchQueueMetrics();
        } catch (ApiException e) {
            System.out.println("HTTP Status: " + e.getStatusCode());
            System.out.println("Response Body: " + e.getRawBody());
            System.out.println("Message: " + e.getMessage());
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

}
