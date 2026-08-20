package com.genesys.service.component;

import com.genesys.config.properties.GenesysProperties;
import com.genesys.factory.RoutingApiFactory;
import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.RoutingApi;
import com.mypurecloud.sdk.v2.api.request.GetRoutingQueuesRequest;
import com.mypurecloud.sdk.v2.model.Queue;
import com.mypurecloud.sdk.v2.model.QueueEntityListing;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenesysQueueDirectoryClient {

    private final GenesysAuthService genesysAuthService;
    private final GenesysProperties genesysProperties;
    private final RoutingApiFactory routingApiFactory;

    public GenesysQueueDirectoryClient(GenesysAuthService genesysAuthService,
                                     GenesysProperties genesysProperties,
                                     RoutingApiFactory routingApiFactory){
        this.genesysAuthService=genesysAuthService;
        this.genesysProperties=genesysProperties;
        this.routingApiFactory=routingApiFactory;
    }

    public List<Queue> fetchQueues() throws IOException, ApiException {

        List<Queue> queueList = new ArrayList<>();

        RoutingApi routingApi = routingApiFactory.create(genesysAuthService.getValidClient());

        int pageNum=1;
        while(true){


            //. division id is given
            GetRoutingQueuesRequest request = GetRoutingQueuesRequest.builder()
                    .withPageSize(100)
                    .withSortOrder("asc")
                    .withDivisionId(List.of(genesysProperties.division()))
                    .withPageNumber(pageNum)
                    .build();

            QueueEntityListing page = routingApi.getRoutingQueues(request);

            if(page.getEntities() == null || page.getEntities().isEmpty()){

                break;
            }

            queueList.addAll(page.getEntities());


            if (page.getPageCount() == null || pageNum >= page.getPageCount()) {

                break;
            }
            pageNum++;


        }
        

        return queueList;
    }
}
