package com.skipps.gpu_farm_manager.exception;

public class InferenceServiceException extends RuntimeException {
    public InferenceServiceException(String message) {
        super(message);
    }

    public InferenceServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
