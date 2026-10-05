package com.skipps.gpu_farm_manager.gpu.dto;

public record GpuResponse(
    Long id,
    String gpuNodeHostname,
    String vendor,
    String model,
    String architecture,
    Long vramCapacity,
    String computeCapability,
    String status
) {
}
