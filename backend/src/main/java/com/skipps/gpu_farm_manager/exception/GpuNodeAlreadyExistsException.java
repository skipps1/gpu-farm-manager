package com.skipps.gpu_farm_manager.exception;

public class GpuNodeAlreadyExistsException extends ResourceAlreadyExistsException {
    public GpuNodeAlreadyExistsException(String message) {
        super(message);
    }

    public static GpuNodeAlreadyExistsException withName(String name) {
        return new GpuNodeAlreadyExistsException("GPU node already exists with name: '" + name + "'");
    }

    public static GpuNodeAlreadyExistsException withHostname(String hostname) {
        return new GpuNodeAlreadyExistsException("GPU node already exists with hostname: '" + hostname + "'");
    }
}
