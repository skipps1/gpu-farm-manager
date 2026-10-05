package com.skipps.gpu_farm_manager.user.dto;

import com.skipps.gpu_farm_manager.user.Role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank
    @Size(min = 3, max = 255)
    String username,

    @NotBlank
    @Size(min = 8, max = 255)
    String password,

    Role role
) {}
