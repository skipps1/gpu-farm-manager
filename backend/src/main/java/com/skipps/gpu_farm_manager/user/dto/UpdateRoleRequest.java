package com.skipps.gpu_farm_manager.user.dto;

import com.skipps.gpu_farm_manager.user.Role;

import jakarta.validation.constraints.NotNull;

public record UpdateRoleRequest(
    @NotNull
    Role role
) {}
