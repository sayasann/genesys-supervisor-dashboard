package com.genesys.controller;

import com.genesys.service.GenesysAuthService;
import com.genesys.service.component.GenesysQueueDirectoryClient;
import com.genesys.service.component.GenesysQueueMetricsClient;
import com.genesys.service.component.QueueObservationAggregator;
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

    public TestController(GenesysAuthService genesysAuthService,GenesysQueueMetricsClient client,
                          GenesysQueueDirectoryClient user,QueueObservationAggregator aggregator){
        this.genesysAuthService =  genesysAuthService;
        this.client=client;
        this.user=user;
        this.aggregator=aggregator;
    }


    @GetMapping("/test/genesys-ping")
    public Object ping() throws ApiException, IOException{

        try {
            List<Queue> queues = user.fetchQueues();
            List<String> queueIds = queues.stream().map(Queue::getId).toList();
            return aggregator.aggregate(client.fetchQueueObservations(queueIds));
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
