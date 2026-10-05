package com.skipps.gpu_farm_manager.gpu.dto;

import jakarta.validation.constraints.Size;

public record UpdateGpuRequest(
    @Size (max = 100)
    String vendor,
    @Size (max = 100)
    String model,
    @Size (max = 100)
    String architecture,
    Long vramCapacity,
    @Size (max = 50)
    String computeCapability
) {
}
