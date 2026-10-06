package com.skipps.gpu_farm_manager.model.dto;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateModelRequest(
    @Size(max = 255)
    String name,

    @Size(max = 100)
    String version,

    @Size(max = 50)
    String parameterSize,

    @Size(max = 50)
    String quantization,

    @Positive
    Long estimatedVram
) {}
