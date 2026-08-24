package com.genesys.schedulers;

import com.genesys.cache.AgentStatusCache;
import com.genesys.service.AgentStatusService;
import com.genesys.utils.AgentStatusInfo;
import com.genesys.utils.AgentStatusResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgentStatusScheduler {

    private final AgentStatusService agentStatusService;
    private final AgentStatusCache cache;


    @Scheduled(fixedRate = 15000)
    public void refreshAgentStatus(){
        try {
            AgentStatusResponse fresh = agentStatusService.fetchAgentStatus();
            cache.updateWithFreshData(
                    fresh.agents(),
                    fresh.interactingCount(),
                    fresh.availableCount(),
                    fresh.offQueueCount()

            );


        } catch (Exception e) {
            log.warn("Agents' info couldn't be updated, its marked as stale! {}",e.getMessage());
            cache.markStale();
        }
    }
}
