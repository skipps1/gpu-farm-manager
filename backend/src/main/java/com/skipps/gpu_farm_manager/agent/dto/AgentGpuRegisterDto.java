package com.skipps.gpu_farm_manager.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AgentGpuRegisterDto(
    @NotBlank String vendor,
    @NotBlank String model,
    String architecture,
    @NotNull @Positive Long vramCapacity,
    String computeCapability
) {}
