package com.genesys.factory;

import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.api.RoutingApi;
import com.mypurecloud.sdk.v2.api.UsersApi;
import org.springframework.stereotype.Component;

@Component
public class UsersApiFactory {

    public UsersApi create(ApiClient apiClient){
        return new UsersApi(apiClient);
    }
}
