package com.skipps.gpu_farm_manager.inferenceservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record CreateInferenceServiceRequest(
    @NotBlank
    @Size(max = 255)
    String name,

    @NotBlank
    @Size(max = 100)
    String backend,

    @NotNull
    Long modelId,

    @NotNull
    Long gpuId,

    @Positive
    Long allocatedVram,

    @Size(max = 255)
    String endpoint
) {}
