package com.skipps.gpu_farm_manager.model.dto;

public record ModelResponse(
    Long id,
    String name,
    String version,
    String parameterSize,
    String quantization,
    Long estimatedVram
) {}
