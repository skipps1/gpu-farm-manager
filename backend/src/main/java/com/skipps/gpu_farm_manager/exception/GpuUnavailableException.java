package com.skipps.gpu_farm_manager.exception;

public class GpuUnavailableException extends RuntimeException {
    public GpuUnavailableException(String message) {
        super(message);
    }

    public GpuUnavailableException(Long id, String reason) {
        super(String.format("GPU with id %d is unavailable: %s", id, reason));
    }
}
