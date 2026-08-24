package com.genesys.controller;

import com.genesys.cache.AgentStatusCache;
import com.genesys.utils.CachedAgentStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class AgentStatusController {

    private final AgentStatusCache cache;

    public AgentStatusController(AgentStatusCache cache){
        this.cache=cache;
    }

    @GetMapping("/agents/status")
    public ResponseEntity<CachedAgentStatus> getAgentStatus(){
        CachedAgentStatus cached = cache.get(); // at first there is a empty object CachedAgentStatus
        if(cached.agents().isEmpty()){
            return ResponseEntity.status(502).body(cached);
        }
        return ResponseEntity.ok(cached);
    }
}
