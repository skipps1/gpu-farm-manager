package com.skipps.gpu_farm_manager.agent.dto;

import java.time.LocalDateTime;

public record AgentHeartbeatResponse(
    String hostname,
    String status,
    LocalDateTime receivedAt
) {}
