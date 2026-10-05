package com.skipps.gpu_farm_manager.exception;

public class ModelAlreadyExistsException extends ResourceAlreadyExistsException {
    public ModelAlreadyExistsException(String message) {
        super(message);
    }

    public static ModelAlreadyExistsException withName(String name) {
        return new ModelAlreadyExistsException("Model already exists with name: " + name);
    }
}
