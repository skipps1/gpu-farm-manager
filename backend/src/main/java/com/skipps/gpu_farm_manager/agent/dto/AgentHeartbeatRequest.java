package com.skipps.gpu_farm_manager.agent.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

public record AgentHeartbeatRequest(
    @NotBlank String hostname,
    String agentVersion,
    Double cpuUtilization,
    Long ramTotal,
    Long ramUsed,
    List<GpuTelemetryDto> gpus
) {}
