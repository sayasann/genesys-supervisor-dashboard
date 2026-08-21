package com.genesys.service;

import com.genesys.service.component.AgentStatusAggregator;
import com.genesys.service.component.GenesysAgentDirectoryClient;
import com.genesys.service.component.GenesysQueueDirectoryClient;
import com.genesys.utils.AgentStatusInfo;
import com.genesys.utils.AgentStatusResponse;
import com.genesys.utils.MembershipResult;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.model.Queue;
import com.mypurecloud.sdk.v2.model.User;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AgentStatusService {

    private final GenesysQueueDirectoryClient queueDirectoryClient;
    private final AgentQueueMembershipService membershipService;
    private final GenesysAgentDirectoryClient agentDirectoryClient;
    private final AgentStatusAggregator agentStatusAggregator;

    public AgentStatusService(GenesysQueueDirectoryClient queueDirectoryClient,
                              AgentQueueMembershipService membershipService,
                              GenesysAgentDirectoryClient agentDirectoryClient,
                              AgentStatusAggregator agentStatusAggregator) {
        this.queueDirectoryClient = queueDirectoryClient;
        this.membershipService = membershipService;
        this.agentDirectoryClient = agentDirectoryClient;
        this.agentStatusAggregator = agentStatusAggregator;
    }


    public AgentStatusResponse fetchAgentStatus() throws IOException, ApiException {
        //divisiona ait queları çek
        List<Queue> queues = queueDirectoryClient.fetchQueues();
        //her queue uyelerini çek, agent->queue name eşleşmesi
        MembershipResult membership= membershipService.fetchMemberships(queues);
        //bu agent idleriyle presence ve routing statusları çek
        List<User> users = agentDirectoryClient.fetchAgentsByIds(membership.agentIds());
        //her agent ve status birleştir kucuk dtoya
        List<AgentStatusAggregator.AgentStatus> statuses =agentStatusAggregator.aggregate(users);

        //durum + queue isimlerini birleştir
        List<AgentStatusInfo> agentInfos = new ArrayList<>();
        int interactingCount = 0;
        int availableCount = 0;
        int offQueueCount = 0;

        for(AgentStatusAggregator.AgentStatus status:statuses){
            List<String> queueNames = membership.queueNamesByAgentId().get(status.agentId());
            if(queueNames==null) queueNames=List.of();

            agentInfos.add(new AgentStatusInfo(
                    status.agentId(),
                    status.agentName(),
                    status.status(),
                    queueNames
            ));
            if ("INTERACTING".equals(status.status())) {
                interactingCount++;
            } else if ("AVAILABLE".equals(status.status())) {
                availableCount++;
            } else if ("OFF_QUEUE".equals(status.status())) {
                offQueueCount++;
            }
        }
        return new AgentStatusResponse(agentInfos, interactingCount, availableCount, offQueueCount);
    }
}
