package com.skipps.gpu_farm_manager.scheduler;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.NoCompatibleGpuException;
import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.gpu.GpuRepository;
import com.skipps.gpu_farm_manager.gpu.GpuStatus;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeStatus;
import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceModel;
import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceRepository;
import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceStatus;
import com.skipps.gpu_farm_manager.model.ModelModel;

@Service
@Transactional
public class GpuSchedulerService {

    private static final Logger log = LoggerFactory.getLogger(GpuSchedulerService.class);

    private final GpuRepository gpuRepository;
    private final InferenceServiceRepository serviceRepository;

    public GpuSchedulerService(GpuRepository gpuRepository, InferenceServiceRepository serviceRepository) {
        this.gpuRepository = gpuRepository;
        this.serviceRepository = serviceRepository;
    }

    public ScheduleDecision schedule(ModelModel model, Long requestedVram) {
        long requiredVram = requestedVram != null ? requestedVram : model.getEstimatedVram();

        // 1. Try to find an existing active service running this model on an ONLINE node
        Optional<InferenceServiceModel> existingService = serviceRepository.findAllByModelId(model.getId())
            .stream()
            .filter(s -> s.getStatus() == InferenceServiceStatus.RUNNING || s.getStatus() == InferenceServiceStatus.STARTING)
            .filter(s -> s.getGpu() != null && s.getGpu().getStatus() == GpuStatus.AVAILABLE)
            .filter(s -> s.getGpu().getGpuNode() != null && s.getGpu().getGpuNode().getStatus() == GpuNodeStatus.ONLINE)
            .findFirst();

        if (existingService.isPresent()) {
            InferenceServiceModel service = existingService.get();
            GpuModel gpu = service.getGpu();
            log.info("Assigned workload for model '{}' [ID: {}] to existing active inference service '{}' on GPU {} ({})",
                model.getName(), model.getId(), service.getName(), gpu.getId(), gpu.getModel());
            return new ScheduleDecision(gpu, service, requiredVram);
        }

        // 2. Select a suitable GPU using Best-Fit strategy based on available VRAM
        List<GpuCandidate> eligibleGpus = gpuRepository.findAllWithNode().stream()
            .filter(gpu -> gpu.getStatus() == GpuStatus.AVAILABLE)
            .filter(gpu -> gpu.getGpuNode() != null && gpu.getGpuNode().getStatus() == GpuNodeStatus.ONLINE)
            .map(gpu -> {
                long allocatedVram = serviceRepository.sumAllocatedVramByGpuId(gpu.getId());
                long availableVram = gpu.getVramCapacity() - allocatedVram;
                return new GpuCandidate(gpu, availableVram);
            })
            .filter(candidate -> candidate.availableVram() >= requiredVram)
            .sorted(Comparator.comparingLong(GpuCandidate::availableVram))
            .toList();

        if (eligibleGpus.isEmpty()) {
            log.warn("Scheduling failed for model '{}' (required VRAM: {} MB): no GPU with sufficient available VRAM found",
                model.getName(), requiredVram);
            throw NoCompatibleGpuException.forWorkload(model.getName(), requiredVram);
        }

        GpuModel chosenGpu = eligibleGpus.get(0).gpu();

        // 3. Provision inference service on selected GPU
        InferenceServiceModel newService = new InferenceServiceModel();
        newService.setName(String.format("%s-%d-%d", model.getName(), chosenGpu.getId(), System.currentTimeMillis()));
        newService.setBackend("OLLAMA");
        newService.setModel(model);
        newService.setGpu(chosenGpu);
        newService.setStatus(InferenceServiceStatus.STARTING);
        newService.setAllocatedVram(requiredVram);
        newService.setEndpoint(String.format("http://%s:11434", chosenGpu.getGpuNode().getHostname()));
        newService = serviceRepository.save(newService);

        log.info("Scheduled workload for model '{}' (required VRAM: {} MB) onto GPU {} ({}) on node '{}'. Created service '{}'",
            model.getName(), requiredVram, chosenGpu.getId(), chosenGpu.getModel(), chosenGpu.getGpuNode().getHostname(), newService.getName());

        return new ScheduleDecision(chosenGpu, newService, requiredVram);
    }

    private record GpuCandidate(GpuModel gpu, long availableVram) {}
}
