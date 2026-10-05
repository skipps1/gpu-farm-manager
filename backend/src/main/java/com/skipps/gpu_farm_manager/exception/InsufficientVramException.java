package com.skipps.gpu_farm_manager.exception;

public class InsufficientVramException extends InsufficientResourcesException {
    public InsufficientVramException(String message) {
        super(message);
    }

    public InsufficientVramException(long requestedVram, long availableVram) {
        super(String.format("Insufficient VRAM: requested %d MB, but only %d MB available", requestedVram, availableVram));
    }
}
