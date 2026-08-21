package com.genesys.service.component;

import com.mypurecloud.sdk.v2.model.User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AgentStatusAggregator {


    private static final String STATUS_IDLE = "IDLE";
    private static final String STATUS_OFF_QUEUE = "OFF_QUEUE";

    public record AgentStatus(
            String agentId,
            String agentName,
            String status
    ){}

    private String categorize(String routingStatus){
        if(STATUS_OFF_QUEUE.equalsIgnoreCase(routingStatus)){
            return "OFF_QUEUE";
        }
        if(STATUS_IDLE.equalsIgnoreCase(routingStatus)){
            return "AVAILABLE";
        }

        return "INTERACTING";
    }

    private String extractRoutingStatus(User user){
        if(user.getRoutingStatus()==null || user.getRoutingStatus().getStatus()==null) return "";

        return user.getRoutingStatus().getStatus().toString();

    }

    public List<AgentStatus> aggregate(List<User> users){
        List<AgentStatus> result = new ArrayList<>();

        for(User user: users){
            String routingStatus = extractRoutingStatus(user);
            String category = categorize(routingStatus);

            result.add(new AgentStatus(user.getId(),user.getName(),category));
        }

        return result;
    }

}
