package com.skipps.gpu_farm_manager.exception;

public class InferenceServiceNotFoundException extends ResourceNotFoundException {
    public InferenceServiceNotFoundException(String message) {
        super(message);
    }

    public static InferenceServiceNotFoundException withId(Long id) {
        return new InferenceServiceNotFoundException("Inference service not found with id: " + id);
    }

    public static InferenceServiceNotFoundException withName(String name) {
        return new InferenceServiceNotFoundException("Inference service not found with name: " + name);
    }
}
