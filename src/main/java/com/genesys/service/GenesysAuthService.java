package com.genesys.service;

import com.genesys.config.properties.GenesysProperties;
import com.genesys.exception.BaseException;
import com.genesys.exception.ErrorMessage;
import com.genesys.exception.MessageType;
import com.mypurecloud.sdk.v2.ApiClient;
import com.mypurecloud.sdk.v2.ApiException;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;

@Service
@Slf4j
public class GenesysAuthService {

    private final ApiClient apiClient;
    private final GenesysProperties properties;

    private Instant expiresAt = Instant.EPOCH;

    public GenesysAuthService(ApiClient apiClient, GenesysProperties properties){
        this.apiClient=apiClient;
        this.properties=properties;
    }


    public synchronized ApiClient getValidClient(){

        if(Instant.now().isBefore(expiresAt.minusSeconds(60))){
            return apiClient;
        }


        try{
            long expiresInSecond = apiClient
                    .authorizeClientCredentials(properties.clientId(), properties.clientSecret())
                    .getBody()
                    .getExpires_in();

            expiresAt = Instant.now().plusSeconds(expiresInSecond);
            log.info("Genesys token is refreshed, expire time: {}", expiresAt);

            return apiClient;


        } catch (ApiException  |IOException e) {
            log.error("Genesys token couldn't be fetched. HTTP status: {}", e.getClass().getSimpleName());
            throw new BaseException(new ErrorMessage(MessageType.GENESYS_AUTH_FAILED, ""));
        }





    }



}
