package com.skipps.gpu_farm_manager.gpu.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGpuRequest(
    @NotBlank
    @Size(min = 8, max = 255)
    String gpuNodeHostname,
    @NotBlank
    @Size(max = 100)
    String vendor,
    @NotBlank
    @Size(max = 100)
    String model,
    String architecture,
    @NotNull
    Long vramCapacity,
    @NotBlank
    @Size(max = 50)
    String computeCapability
) {
}
