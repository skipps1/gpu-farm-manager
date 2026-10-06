package com.skipps.gpu_farm_manager.agent.dto;

public record GpuTelemetryDto(
    Long gpuId,
    String model,
    Double utilization,
    Long vramTotal,
    Long vramUsed,
    Long vramFree,
    Double temperature,
    Double powerUsageWatts
) {}
