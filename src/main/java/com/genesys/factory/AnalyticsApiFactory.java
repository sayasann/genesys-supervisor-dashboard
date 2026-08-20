package com.genesys.factory;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.api.AnalyticsApi;
import org.springframework.stereotype.Component;

@Component
public class AnalyticsApiFactory {

    public AnalyticsApi create(ApiClient apiClient){
        return new AnalyticsApi(apiClient);
    }
}
