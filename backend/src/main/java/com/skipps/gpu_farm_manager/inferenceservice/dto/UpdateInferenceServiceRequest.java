package com.skipps.gpu_farm_manager.inferenceservice.dto;

import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceStatus;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record UpdateInferenceServiceRequest(
    @Size(max = 255)
    String name,

    InferenceServiceStatus status,

    @Positive
    Long allocatedVram,

    @Size(max = 255)
    String endpoint
) {}
