package com.skipps.gpu_farm_manager.workload;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.InvalidWorkloadStateException;
import com.skipps.gpu_farm_manager.exception.ModelNotFoundException;
import com.skipps.gpu_farm_manager.exception.UserNotFoundException;
import com.skipps.gpu_farm_manager.exception.WorkloadNotFoundException;
import com.skipps.gpu_farm_manager.model.ModelModel;
import com.skipps.gpu_farm_manager.model.ModelRepository;
import com.skipps.gpu_farm_manager.scheduler.GpuSchedulerService;
import com.skipps.gpu_farm_manager.scheduler.ScheduleDecision;
import com.skipps.gpu_farm_manager.user.UserModel;
import com.skipps.gpu_farm_manager.user.UserRepository;
import com.skipps.gpu_farm_manager.workload.dto.SubmitWorkloadRequest;
import com.skipps.gpu_farm_manager.workload.dto.WorkloadResponse;

@Service
@Transactional
public class WorkloadService {

    private static final Logger log = LoggerFactory.getLogger(WorkloadService.class);

    private final WorkloadRepository workloadRepository;
    private final ModelRepository modelRepository;
    private final UserRepository userRepository;
    private final GpuSchedulerService schedulerService;

    public WorkloadService(
        WorkloadRepository workloadRepository,
        ModelRepository modelRepository,
        UserRepository userRepository,
        GpuSchedulerService schedulerService
    ) {
        this.workloadRepository = workloadRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
        this.schedulerService = schedulerService;
    }

    public WorkloadResponse submitWorkload(SubmitWorkloadRequest request) {
        String currentUsername = getCurrentUsername();
        UserModel user = userRepository.findByUsername(currentUsername)
            .orElseThrow(() -> UserNotFoundException.withUsername(currentUsername));

        ModelModel model = modelRepository.findById(request.modelId())
            .orElseThrow(() -> new ModelNotFoundException(request.modelId()));

        long requiredVram = request.requestedVram() != null ? request.requestedVram() : model.getEstimatedVram();
        WorkloadPriority priority = request.priority() != null ? request.priority() : WorkloadPriority.NORMAL;

        // Schedule onto suitable GPU / Inference service
        ScheduleDecision decision = schedulerService.schedule(model, requiredVram);

        WorkloadModel workload = new WorkloadModel();
        workload.setUser(user);
        workload.setModel(model);
        workload.setGpu(decision.gpu());
        workload.setService(decision.service());
        workload.setStatus(WorkloadStatus.RUNNING);
        workload.setPriority(priority);
        workload.setRequestedVram(decision.allocatedVram());
        workload.setStartedAt(LocalDateTime.now());

        workload = workloadRepository.save(workload);

        log.info("Workload submitted [ID: {}] by user '{}' for model '{}': RUNNING on GPU {} ({})",
            workload.getId(), user.getUsername(), model.getName(), decision.gpu().getId(), decision.gpu().getModel());

        return mapToResponse(workload);
    }

    @Transactional(readOnly = true)
    public WorkloadResponse getWorkload(Long id) {
        WorkloadModel workload = workloadRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new WorkloadNotFoundException(id));
        return mapToResponse(workload);
    }

    @Transactional(readOnly = true)
    public List<WorkloadResponse> getAllWorkloads() {
        return workloadRepository.findAllWithDetails().stream()
            .map(this::mapToResponse)
            .toList();
    }

    @Transactional(readOnly = true)
    public List<WorkloadResponse> getMyWorkloads() {
        String currentUsername = getCurrentUsername();
        return workloadRepository.findAllByUsernameWithDetails(currentUsername).stream()
            .map(this::mapToResponse)
            .toList();
    }

    public WorkloadResponse cancelWorkload(Long id) {
        WorkloadModel workload = workloadRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new WorkloadNotFoundException(id));

        if (workload.getStatus() == WorkloadStatus.COMPLETED || workload.getStatus() == WorkloadStatus.CANCELLED) {
            throw new InvalidWorkloadStateException(id, workload.getStatus().name(), "CANCEL");
        }

        workload.setStatus(WorkloadStatus.CANCELLED);
        workload.setFinishedAt(LocalDateTime.now());
        workload = workloadRepository.save(workload);

        log.info("Workload {} cancelled", id);
        return mapToResponse(workload);
    }

    public WorkloadResponse completeWorkload(Long id) {
        WorkloadModel workload = workloadRepository.findByIdWithDetails(id)
            .orElseThrow(() -> new WorkloadNotFoundException(id));

        if (workload.getStatus() != WorkloadStatus.RUNNING && workload.getStatus() != WorkloadStatus.PENDING) {
            throw new InvalidWorkloadStateException(id, workload.getStatus().name(), "COMPLETE");
        }

        workload.setStatus(WorkloadStatus.COMPLETED);
        workload.setFinishedAt(LocalDateTime.now());
        workload = workloadRepository.save(workload);

        log.info("Workload {} completed successfully", id);
        return mapToResponse(workload);
    }

    public void deleteWorkload(Long id) {
        WorkloadModel workload = workloadRepository.findById(id)
            .orElseThrow(() -> new WorkloadNotFoundException(id));

        workloadRepository.delete(workload);
        log.info("Deleted workload record {}", id);
    }

    private String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return "system";
        }
        return auth.getName();
    }

    private WorkloadResponse mapToResponse(WorkloadModel workload) {
        return new WorkloadResponse(
            workload.getId(),
            workload.getUser() != null ? workload.getUser().getId() : null,
            workload.getUser() != null ? workload.getUser().getUsername() : null,
            workload.getModel() != null ? workload.getModel().getId() : null,
            workload.getModel() != null ? workload.getModel().getName() : null,
            workload.getService() != null ? workload.getService().getId() : null,
            workload.getService() != null ? workload.getService().getName() : null,
            workload.getGpu() != null ? workload.getGpu().getId() : null,
            workload.getGpu() != null ? workload.getGpu().getModel() : null,
            workload.getGpu() != null && workload.getGpu().getGpuNode() != null ? workload.getGpu().getGpuNode().getHostname() : null,
            workload.getStatus() != null ? workload.getStatus().name() : null,
            workload.getPriority() != null ? workload.getPriority().name() : null,
            workload.getRequestedVram(),
            workload.getStartedAt(),
            workload.getFinishedAt()
        );
    }
}
