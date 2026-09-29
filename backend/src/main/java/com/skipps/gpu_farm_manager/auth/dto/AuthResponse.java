package com.skipps.gpu_farm_manager.auth.dto;

import java.time.LocalDateTime;

public record AuthResponse(

    String jwtToken,
    String username,
    String role,
    LocalDateTime expiration
) {
}
