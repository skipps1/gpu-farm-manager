package com.skipps.gpu_farm_manager.exception;

public class NodeUnavailableException extends RuntimeException {
    public NodeUnavailableException(String message) {
        super(message);
    }

    public static NodeUnavailableException withHostname(String hostname, String status) {
        return new NodeUnavailableException(String.format("GPU node '%s' is unavailable (current status: %s)", hostname, status));
    }
}
