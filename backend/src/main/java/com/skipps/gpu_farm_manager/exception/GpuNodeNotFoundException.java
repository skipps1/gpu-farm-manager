package com.skipps.gpu_farm_manager.exception;

public class GpuNodeNotFoundException extends ResourceNotFoundException {
    public GpuNodeNotFoundException(String message) {
        super(message);
    }

    public static GpuNodeNotFoundException withName(String name) {
        return new GpuNodeNotFoundException("GPU node not found with name: " + name);
    }

    public static GpuNodeNotFoundException withHostname(String hostname) {
        return new GpuNodeNotFoundException("GPU node not found with hostname: " + hostname);
    }

    public static GpuNodeNotFoundException withId(Long id) {
        return new GpuNodeNotFoundException("GPU node not found with id: " + id);
    }
}
