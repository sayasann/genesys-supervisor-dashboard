package com.genesys.utils;

import java.util.List;

public record AgentStatusResponse(List<AgentStatusInfo> agents,
                                  int interactingCount,
                                  int availableCount,
                                  int offQueueCount) {
}
