package com.skipps.gpu_farm_manager.exception;

public class QuotaExceededException extends RuntimeException {
    public QuotaExceededException(String message) {
        super(message);
    }

    public static QuotaExceededException tokenLimitReached(long currentTokens, long maxTokens) {
        return new QuotaExceededException(String.format("Token quota exceeded: %d / %d tokens used", currentTokens, maxTokens));
    }

    public static QuotaExceededException vramLimitReached(long requestedVram, long availableQuotaVram) {
        return new QuotaExceededException(String.format("VRAM quota exceeded: requested %d MB, remaining quota is %d MB", requestedVram, availableQuotaVram));
    }

    public static QuotaExceededException concurrentWorkloadsLimitReached(int current, int max) {
        return new QuotaExceededException(String.format("Concurrent workload quota exceeded: %d / %d active workloads", current, max));
    }

    public static QuotaExceededException gpuCountLimitReached(int currentGpus, int maxGpus) {
        return new QuotaExceededException(String.format("GPU count quota exceeded: %d / %d GPUs allocated", currentGpus, maxGpus));
    }
}
