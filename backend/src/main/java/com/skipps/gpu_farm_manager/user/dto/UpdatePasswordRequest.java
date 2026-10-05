package com.skipps.gpu_farm_manager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdatePasswordRequest(
    String currentPassword,

    @NotBlank
    @Size(min = 8, max = 255)
    String newPassword
) {}
