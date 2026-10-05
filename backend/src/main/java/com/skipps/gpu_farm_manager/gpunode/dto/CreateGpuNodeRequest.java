package com.skipps.gpu_farm_manager.gpunode.dto;

import com.skipps.gpu_farm_manager.gpunode.GpuNodeStatus;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CreateGpuNodeRequest
(
    @NotBlank
    @Size(min = 8, max = 255)
    String name,

    @NotBlank
    @Size(min = 8, max = 255)
    String hostName,

    @NotNull
    GpuNodeStatus status,

    @Size(max = 50)
    String agentVersion
) {}
