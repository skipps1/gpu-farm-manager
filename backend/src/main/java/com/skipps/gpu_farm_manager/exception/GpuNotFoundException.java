package com.skipps.gpu_farm_manager.exception;

public class GpuNotFoundException extends ResourceNotFoundException {
    public GpuNotFoundException(String message) {
        super(message);
    }

    public GpuNotFoundException(Long id) {
        super("GPU not found with id: " + id);
    }
}
