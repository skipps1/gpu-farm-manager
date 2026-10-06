package com.skipps.gpu_farm_manager.agent.dto;

import java.time.LocalDateTime;

public record AgentRegisterResponse(
    Long nodeId,
    String hostname,
    String status,
    int registeredGpuCount,
    String message,
    LocalDateTime registeredAt
) {}
