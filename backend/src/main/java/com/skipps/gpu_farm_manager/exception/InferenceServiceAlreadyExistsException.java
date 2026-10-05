package com.skipps.gpu_farm_manager.exception;

public class InferenceServiceAlreadyExistsException extends ResourceAlreadyExistsException {
    public InferenceServiceAlreadyExistsException(String message) {
        super(message);
    }

    public static InferenceServiceAlreadyExistsException withName(String name) {
        return new InferenceServiceAlreadyExistsException("Inference service already exists with name: " + name);
    }
}
