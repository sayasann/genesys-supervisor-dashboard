package com.genesys.controller;

import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.RoutingApi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController {


    private final GenesysAuthService genesysAuthService;

    public TestController(GenesysAuthService genesysAuthService){
        this.genesysAuthService =  genesysAuthService;
    }


    @GetMapping("/test/genesys-ping")
    public Object ping() throws ApiException, IOException{
        RoutingApi routingApi = new RoutingApi(genesysAuthService.getValidClient());
        return routingApi.getRoutingQueues(1, 25, null, null, null, null, null, null, null, null);
    }

}
