package com.skipps.gpu_farm_manager.inferenceservice.dto;

public record InferenceServiceResponse(
    Long id,
    String name,
    String backend,
    Long modelId,
    String modelName,
    Long gpuId,
    String gpuModel,
    String gpuNodeHostname,
    String status,
    Long allocatedVram,
    String endpoint
) {}
