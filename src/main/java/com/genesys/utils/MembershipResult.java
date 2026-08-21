package com.genesys.utils;

import java.util.List;
import java.util.Map;
import java.util.Set;

public record MembershipResult(
        Map<String, List<String>> queueNamesByAgentId,
        Set<String> agentIds
) {
}
