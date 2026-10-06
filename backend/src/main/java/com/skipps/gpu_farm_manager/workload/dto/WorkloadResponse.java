package com.skipps.gpu_farm_manager.workload.dto;

import java.time.LocalDateTime;

public record WorkloadResponse(
    Long id,
    Long userId,
    String username,
    Long modelId,
    String modelName,
    Long serviceId,
    String serviceName,
    Long gpuId,
    String gpuModel,
    String gpuNodeHostname,
    String status,
    String priority,
    Long requestedVram,
    LocalDateTime startedAt,
    LocalDateTime finishedAt
) {}
