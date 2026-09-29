package com.skipps.gpu_farm_manager.auth.dto;

import com.skipps.gpu_farm_manager.user.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
    @NotBlank
    @Size(min = 8, max = 255)
    String username,
    @NotBlank
    @Size(min = 8, max = 255)
    String password,
    Role role
) {
}
