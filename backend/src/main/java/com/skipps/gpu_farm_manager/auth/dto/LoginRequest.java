package com.skipps.gpu_farm_manager.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank
    @Size(min = 8, max = 255)
    String username,
    @NotBlank
    @Size(min = 8, max = 255)
    String password
) {
}
