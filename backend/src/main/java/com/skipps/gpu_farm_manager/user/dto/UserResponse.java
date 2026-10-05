package com.skipps.gpu_farm_manager.user.dto;

import java.time.LocalDateTime;

public record UserResponse(
    Long id,
    String username,
    String role,
    LocalDateTime createdAt
) {}
