package com.skipps.gpu_farm_manager.scheduler;

import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceModel;

public record ScheduleDecision(
    GpuModel gpu,
    InferenceServiceModel service,
    long allocatedVram
) {}
