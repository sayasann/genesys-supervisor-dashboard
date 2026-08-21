package com.genesys.service;

import com.genesys.service.component.GenesysQueueMemberClient;
import com.genesys.utils.MembershipResult;
import com.mypurecloud.sdk.v2.ApiException;
import com.mypurecloud.sdk.v2.model.Queue;
import com.mypurecloud.sdk.v2.model.QueueMember;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;

@Service
public class AgentQueueMembershipService {

    private final GenesysQueueMemberClient queueMemberClient;

    public AgentQueueMembershipService(GenesysQueueMemberClient queueMemberClient) {
        this.queueMemberClient = queueMemberClient;
    }

    public MembershipResult fetchMemberships(List<Queue> queues) throws IOException, ApiException {

        Map<String, List<String>> queueNamesByAgentId = new HashMap<>();
        Set<String> agentIds = new HashSet<>();

        for(Queue queue: queues){
            List<QueueMember> members = queueMemberClient.fetchMembers(queue.getId());

            for(QueueMember member: members){
                String agentId = member.getId();
                agentIds.add(agentId);

                if(!queueNamesByAgentId.containsKey(agentId)){
                    queueNamesByAgentId.put(agentId,new ArrayList<>());
                }
                queueNamesByAgentId.get(agentId).add(queue.getName());
            }
        }

        return new MembershipResult(queueNamesByAgentId,agentIds);
    }


}
