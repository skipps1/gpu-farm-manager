package com.skipps.gpu_farm_manager.exception;

public class ModelNotFoundException extends ResourceNotFoundException {
    public ModelNotFoundException(String message) {
        super(message);
    }

    public ModelNotFoundException(Long id) {
        super("Model not found with id: " + id);
    }

    public static ModelNotFoundException withName(String name) {
        return new ModelNotFoundException("Model not found with name: " + name);
    }

    public static ModelNotFoundException withId(Long id) {
        return new ModelNotFoundException(id);
    }
}
