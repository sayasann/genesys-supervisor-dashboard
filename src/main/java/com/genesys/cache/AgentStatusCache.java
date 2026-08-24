package com.genesys.cache;

import com.genesys.utils.AgentStatusInfo;
import com.genesys.utils.CachedAgentStatus;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class AgentStatusCache {

    private final AtomicReference<CachedAgentStatus> ref =
            new AtomicReference<>(CachedAgentStatus.empty());


    public void updateWithFreshData(List<AgentStatusInfo> agents,
                                    int interactingCount,
                                    int availableCount,
                                    int offQueueCount){

        ref.set(new CachedAgentStatus(
                agents,interactingCount,availableCount,offQueueCount, Instant.now(),false
        ));
    }

    public void markStale(){
        CachedAgentStatus current = ref.get();
        if(current.agents().isEmpty()){
            return;
        }

        ref.set(new CachedAgentStatus(
                current.agents(),
                current.interactingCount(),
                current.availableCount(),
                current.offQueueCount(),
                current.lastSuccessfulFetch(),
                true
        ));
    }

    public CachedAgentStatus get(){
        return ref.get();
    }
}
