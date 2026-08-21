package com.genesys.service.component;

import com.genesys.factory.RoutingApiFactory;
import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.RoutingApi;
import com.mypurecloud.sdk.v2.model.QueueMember;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class GenesysQueueMemberClient {

    private final GenesysAuthService genesysAuthService;
    private final RoutingApiFactory routingApiFactory;

    public GenesysQueueMemberClient(GenesysAuthService genesysAuthService,
                                    RoutingApiFactory routingApiFactory) {
        this.genesysAuthService = genesysAuthService;
        this.routingApiFactory = routingApiFactory;
    }

    public List<QueueMember> fetchMembers(String queueId) throws IOException, ApiException {

        RoutingApi routingApi = routingApiFactory.create(genesysAuthService.getValidClient());
        List<QueueMember> allMembers = new ArrayList<>();
        int pageNumber = 1;
        int pageSize = 100;

        while(true){

            var page = routingApi.getRoutingQueueMembers(
                    queueId,pageNumber,pageSize,null,null,null,null,null,null,null,null,null,null
            );

            if(page.getEntities()==null || page.getEntities().isEmpty()){
                break;
            }
            allMembers.addAll(page.getEntities());
            if(pageNumber>=page.getPageNumber()) break;
            pageNumber++;


        }
        return allMembers;
    }
}
