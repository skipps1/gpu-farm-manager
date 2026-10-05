package com.skipps.gpu_farm_manager.exception;

public class InvalidWorkloadStateException extends RuntimeException {
    public InvalidWorkloadStateException(String message) {
        super(message);
    }

    public InvalidWorkloadStateException(Long workloadId, String currentState, String attemptedAction) {
        super(String.format("Cannot perform action '%s' on workload %d in state '%s'", attemptedAction, workloadId, currentState));
    }
}
