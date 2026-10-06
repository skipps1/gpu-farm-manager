package com.skipps.gpu_farm_manager.workload.dto;

import com.skipps.gpu_farm_manager.workload.WorkloadPriority;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record SubmitWorkloadRequest(
    @NotNull
    Long modelId,

    WorkloadPriority priority,

    @Positive
    Long requestedVram,

    Long serviceId,

    Long gpuId
) {}
