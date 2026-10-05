package com.skipps.gpu_farm_manager.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUsernameRequest(
    @NotBlank
    @Size(min = 3, max = 255)
    String newUsername
) {}
