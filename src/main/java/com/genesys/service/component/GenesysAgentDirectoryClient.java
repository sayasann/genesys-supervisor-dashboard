package com.genesys.service.component;

import com.genesys.factory.UsersApiFactory;
import com.genesys.service.GenesysAuthService;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.api.UsersApi;
import com.mypurecloud.sdk.v2.model.User;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class GenesysAgentDirectoryClient {
    private final GenesysAuthService genesysAuthService;
    private final UsersApiFactory usersApiFactory;

    public GenesysAgentDirectoryClient(GenesysAuthService genesysAuthService,
                                       UsersApiFactory usersApiFactory) {
        this.genesysAuthService = genesysAuthService;
        this.usersApiFactory = usersApiFactory;
    }


    public List<User> fetchAgentsByIds(Set<String> agentIds) throws IOException, ApiException {

        UsersApi usersApi = usersApiFactory.create(genesysAuthService.getValidClient());
        List<String> idList = List.copyOf(agentIds);
        List<String> expand = List.of("presence", "routingStatus");

        List<User> allUsers = new ArrayList<>();
        int pageNumber = 1;
        int pageSize = 100;

        while (true) {
            var page = usersApi.getUsers(
                    pageSize,
                    pageNumber,
                    idList,      // id
                    null,        // jabberId
                    null,        // sortOrder
                    expand,      // expand
                    null,        // integrationPresenceSource
                    null,        // userCustomAttributeSchemaIds
                    null         // state
            );

            if (page.getEntities() == null || page.getEntities().isEmpty()) {
                break;
            }
            allUsers.addAll(page.getEntities());

            if (pageNumber >= page.getPageCount()) {
                break;
            }
            pageNumber++;
        }

        return allUsers;

    }

}
