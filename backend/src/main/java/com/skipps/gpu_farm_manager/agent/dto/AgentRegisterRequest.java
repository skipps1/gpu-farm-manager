package com.skipps.gpu_farm_manager.agent.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

public record AgentRegisterRequest(
    String name,
    @NotBlank String hostname,
    String agentVersion,
    @NotEmpty @Valid List<AgentGpuRegisterDto> gpus
) {}
