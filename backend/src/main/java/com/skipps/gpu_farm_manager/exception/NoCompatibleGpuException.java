package com.skipps.gpu_farm_manager.exception;

public class NoCompatibleGpuException extends InsufficientResourcesException {
    public NoCompatibleGpuException(String message) {
        super(message);
    }

    public static NoCompatibleGpuException forWorkload(String modelName, long requiredVram) {
        return new NoCompatibleGpuException(String.format("No compatible GPU found with at least %d MB available VRAM for model '%s'", requiredVram, modelName));
    }
}
