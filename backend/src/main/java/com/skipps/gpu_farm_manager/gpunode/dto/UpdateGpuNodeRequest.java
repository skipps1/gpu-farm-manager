package com.skipps.gpu_farm_manager.gpunode.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateGpuNodeRequest
(
    @NotBlank
    @Size (min = 8, max = 255)
    String name,

    @NotBlank
    @Size(min = 8, max = 255)
    String hostName,

    @Size(max = 50)
    String agentVersion
) {}
