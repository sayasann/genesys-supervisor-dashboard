package com.genesys.factory;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.api.AnalyticsApi;
import com.mypurecloud.sdk.v2.api.RoutingApi;
import org.springframework.stereotype.Component;

@Component
public class RoutingApiFactory {

    public RoutingApi create(ApiClient apiClient){
        return new RoutingApi(apiClient);
    }
}
