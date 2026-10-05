package com.skipps.gpu_farm_manager.exception;

public class QuotaNotFoundException extends ResourceNotFoundException {
    public QuotaNotFoundException(String message) {
        super(message);
    }

    public static QuotaNotFoundException forUser(Long userId) {
        return new QuotaNotFoundException("Quota not found for user id: " + userId);
    }

    public static QuotaNotFoundException forUsername(String username) {
        return new QuotaNotFoundException("Quota not found for user: " + username);
    }
}
