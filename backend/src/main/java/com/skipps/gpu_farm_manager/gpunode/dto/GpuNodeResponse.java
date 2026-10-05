package com.skipps.gpu_farm_manager.gpunode.dto;

import java.time.LocalDateTime;

public record GpuNodeResponse(
    Long id,
    String name,
    String hostname,
    String status,
    String agentVersion,
    LocalDateTime createdAt
) {
}
