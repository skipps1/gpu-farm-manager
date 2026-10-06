package com.skipps.gpu_farm_manager.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateModelRequest(
    @NotBlank
    @Size(max = 255)
    String name,

    @NotBlank
    @Size(max = 100)
    String version,

    @Size(max = 50)
    String parameterSize,

    @Size(max = 50)
    String quantization,

    @NotNull
    @Positive
    Long estimatedVram
) {}
