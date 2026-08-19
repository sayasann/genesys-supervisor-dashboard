package com.genesys.config;

import com.genesys.config.properties.GenesysProperties;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.PureCloudRegionHosts;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GenesysApiConfig {

    private final GenesysProperties properties;

    public GenesysApiConfig(GenesysProperties properties) {
        this.properties = properties;
    }

    //if exception is thrown here app is not even up because its in bean
    private PureCloudRegionHosts resolveRegion(String region){

        try{
            return PureCloudRegionHosts.valueOf(region.replace('-','_'));
        }catch (IllegalArgumentException e){
            throw new IllegalStateException(
                    "Invalid Genesys region: '" + region + "'. " +
                            "check region value in application.properties", e);
        }
    }

    @Bean
    public ApiClient genesysApiClient(){
        PureCloudRegionHosts regionHost = resolveRegion(properties.region());

        return ApiClient.Builder.standard()
                .withBasePath(regionHost)
                .build();
    }
}
