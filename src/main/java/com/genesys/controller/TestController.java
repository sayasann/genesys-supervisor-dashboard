package com.genesys.controller;

import com.genesys.service.AgentQueueMembershipService;
import com.genesys.service.AgentStatusService;
import com.genesys.service.GenesysAuthService;
import com.genesys.service.QueueMetricsService;
import com.genesys.service.component.*;
import com.mypurecloud.sdk.v2.ApiException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {


    private final GenesysAuthService genesysAuthService;
    private final GenesysQueueMetricsClient client;
    private final GenesysQueueDirectoryClient user;
    private final QueueObservationAggregator aggregator;
    private final QueueMetricsService queueMetricsService;
    private final GenesysQueueAggregatesClient aggregator2;
    private final ConversationAggregateAggregator aggregator3;
    private final AgentQueueMembershipService membershipService;
    @Autowired
    private GenesysAgentDirectoryClient agentDirectoryClient;
    @Autowired
    private AgentStatusAggregator agentStatusAggregator;

    @Autowired
    private AgentStatusService statusService;

    public TestController(GenesysAuthService genesysAuthService,GenesysQueueMetricsClient client,
                          GenesysQueueDirectoryClient user,QueueObservationAggregator aggregator,
                          QueueMetricsService queueMetricsService, GenesysQueueAggregatesClient aggregator2,
                          ConversationAggregateAggregator conversationAggregateAggregator,
                          AgentQueueMembershipService membershipService){
        this.genesysAuthService =  genesysAuthService;
        this.client=client;
        this.user=user;
        this.aggregator=aggregator;
        this.queueMetricsService = queueMetricsService;
        this.aggregator2=aggregator2;
        this.aggregator3=conversationAggregateAggregator;
        this.membershipService = membershipService;
    }


    @GetMapping("/test/genesys-ping")
    public Object ping() throws ApiException, IOException{

        try {


            return  statusService.fetchAgentStatus();
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
