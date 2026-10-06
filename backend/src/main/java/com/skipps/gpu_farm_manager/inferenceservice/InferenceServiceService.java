package com.skipps.gpu_farm_manager.inferenceservice;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.GpuNotFoundException;
import com.skipps.gpu_farm_manager.exception.InferenceServiceAlreadyExistsException;
import com.skipps.gpu_farm_manager.exception.InferenceServiceNotFoundException;
import com.skipps.gpu_farm_manager.exception.InsufficientVramException;
import com.skipps.gpu_farm_manager.exception.ModelNotFoundException;
import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.gpu.GpuRepository;
import com.skipps.gpu_farm_manager.inferenceservice.dto.CreateInferenceServiceRequest;
import com.skipps.gpu_farm_manager.inferenceservice.dto.InferenceServiceResponse;
import com.skipps.gpu_farm_manager.inferenceservice.dto.UpdateInferenceServiceRequest;
import com.skipps.gpu_farm_manager.model.ModelModel;
import com.skipps.gpu_farm_manager.model.ModelRepository;

@Service
@Transactional
public class InferenceServiceService {

    private static final Logger log = LoggerFactory.getLogger(InferenceServiceService.class);

    private final InferenceServiceRepository repository;
    private final ModelRepository modelRepository;
    private final GpuRepository gpuRepository;

    public InferenceServiceService(
        InferenceServiceRepository repository,
        ModelRepository modelRepository,
        GpuRepository gpuRepository
    ) {
        this.repository = repository;
        this.modelRepository = modelRepository;
        this.gpuRepository = gpuRepository;
    }

    public InferenceServiceResponse createService(CreateInferenceServiceRequest request) {
        if (repository.existsByName(request.name())) {
            throw InferenceServiceAlreadyExistsException.withName(request.name());
        }

        ModelModel model = modelRepository.findById(request.modelId())
            .orElseThrow(() -> new ModelNotFoundException(request.modelId()));

        GpuModel gpu = gpuRepository.findByIdWithNode(request.gpuId())
            .orElseThrow(() -> new GpuNotFoundException(request.gpuId()));

        long requiredVram = request.allocatedVram() != null ? request.allocatedVram() : model.getEstimatedVram();
        long currentAllocated = repository.sumAllocatedVramByGpuId(gpu.getId());
        long availableVram = gpu.getVramCapacity() - currentAllocated;

        if (requiredVram > availableVram) {
            log.warn("Failed to create inference service '{}': requested {} MB VRAM, but GPU {} only has {} MB available",
                request.name(), requiredVram, gpu.getId(), availableVram);
            throw new InsufficientVramException(requiredVram, availableVram);
        }

        InferenceServiceModel service = new InferenceServiceModel();
        service.setName(request.name());
        service.setBackend(request.backend().toUpperCase());
        service.setModel(model);
        service.setGpu(gpu);
        service.setStatus(InferenceServiceStatus.STARTING);
        service.setAllocatedVram(requiredVram);
        service.setEndpoint(request.endpoint());

        service = repository.save(service);

        log.info("Created inference service '{}' [ID: {}] with backend {} for model '{}' on GPU {} ({}) with {} MB VRAM",
            service.getName(), service.getId(), service.getBackend(), model.getName(), gpu.getId(), gpu.getModel(), requiredVram);

        return mapToResponse(service);
    }

    @Transactional(readOnly = true)
    public InferenceServiceResponse getService(Long id) {
        InferenceServiceModel service = repository.findByIdWithDetails(id)
            .orElseThrow(() -> InferenceServiceNotFoundException.withId(id));
        return mapToResponse(service);
    }

    @Transactional(readOnly = true)
    public List<InferenceServiceResponse> getAllServices() {
        return repository.findAllWithDetails().stream()
            .map(this::mapToResponse)
            .toList();
    }

    public InferenceServiceResponse updateService(Long id, UpdateInferenceServiceRequest request) {
        InferenceServiceModel service = repository.findByIdWithDetails(id)
            .orElseThrow(() -> InferenceServiceNotFoundException.withId(id));

        if (request.name() != null && !request.name().equals(service.getName())) {
            if (repository.existsByName(request.name())) {
                throw InferenceServiceAlreadyExistsException.withName(request.name());
            }
            service.setName(request.name());
        }

        if (request.status() != null && request.status() != service.getStatus()) {
            log.info("Inference service {} ('{}') status changed from {} to {}",
                service.getId(), service.getName(), service.getStatus(), request.status());
            service.setStatus(request.status());
        }

        if (request.allocatedVram() != null) {
            service.setAllocatedVram(request.allocatedVram());
        }

        if (request.endpoint() != null) {
            service.setEndpoint(request.endpoint());
        }

        service = repository.save(service);
        return mapToResponse(service);
    }

    public InferenceServiceResponse startService(Long id) {
        InferenceServiceModel service = repository.findByIdWithDetails(id)
            .orElseThrow(() -> InferenceServiceNotFoundException.withId(id));

        service.setStatus(InferenceServiceStatus.RUNNING);
        service = repository.save(service);

        log.info("Inference service {} ('{}') started and marked RUNNING on GPU {} ({})",
            service.getId(), service.getName(), service.getGpu().getId(), service.getGpu().getModel());

        return mapToResponse(service);
    }

    public InferenceServiceResponse stopService(Long id) {
        InferenceServiceModel service = repository.findByIdWithDetails(id)
            .orElseThrow(() -> InferenceServiceNotFoundException.withId(id));

        service.setStatus(InferenceServiceStatus.STOPPED);
        service = repository.save(service);

        log.info("Inference service {} ('{}') stopped", service.getId(), service.getName());

        return mapToResponse(service);
    }

    public void deleteService(Long id) {
        InferenceServiceModel service = repository.findById(id)
            .orElseThrow(() -> InferenceServiceNotFoundException.withId(id));

        repository.delete(service);
        log.info("Deleted inference service {} ('{}')", service.getId(), service.getName());
    }

    private InferenceServiceResponse mapToResponse(InferenceServiceModel service) {
        return new InferenceServiceResponse(
            service.getId(),
            service.getName(),
            service.getBackend(),
            service.getModel() != null ? service.getModel().getId() : null,
            service.getModel() != null ? service.getModel().getName() : null,
            service.getGpu() != null ? service.getGpu().getId() : null,
            service.getGpu() != null ? service.getGpu().getModel() : null,
            service.getGpu() != null && service.getGpu().getGpuNode() != null ? service.getGpu().getGpuNode().getHostname() : null,
            service.getStatus() != null ? service.getStatus().name() : null,
            service.getAllocatedVram(),
            service.getEndpoint()
        );
    }
}
