package com.skipps.gpu_farm_manager.exception;

public class WorkloadNotFoundException extends ResourceNotFoundException {
    public WorkloadNotFoundException(String message) {
        super(message);
    }

    public WorkloadNotFoundException(Long id) {
        super("Workload not found with id: " + id);
    }
}
