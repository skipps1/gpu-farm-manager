package com.skipps.gpu_farm_manager.exception;

public class UnsupportedBackendException extends BadRequestException {
    public UnsupportedBackendException(String message) {
        super(message);
    }

    public static UnsupportedBackendException forBackend(String backend) {
        return new UnsupportedBackendException("Unsupported inference backend: '" + backend + "'. Supported backends are: OLLAMA, VLLM");
    }
}
