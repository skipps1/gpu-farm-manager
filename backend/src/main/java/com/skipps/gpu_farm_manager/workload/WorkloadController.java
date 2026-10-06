package com.skipps.gpu_farm_manager.workload;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.gpu_farm_manager.workload.dto.SubmitWorkloadRequest;
import com.skipps.gpu_farm_manager.workload.dto.WorkloadResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/workloads")
public class WorkloadController {

    private final WorkloadService workloadService;

    public WorkloadController(WorkloadService workloadService) {
        this.workloadService = workloadService;
    }

    @PostMapping
    public ResponseEntity<WorkloadResponse> submitWorkload(@RequestBody @Valid SubmitWorkloadRequest request) {
        return ResponseEntity.ok(workloadService.submitWorkload(request));
    }

    @GetMapping
    public ResponseEntity<List<WorkloadResponse>> getWorkloads(Authentication authentication) {
        boolean isAdminOrOperator = authentication != null && authentication.getAuthorities().stream()
            .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRATOR") || a.getAuthority().equals("ROLE_OPERATOR"));

        if (isAdminOrOperator) {
            return ResponseEntity.ok(workloadService.getAllWorkloads());
        } else {
            return ResponseEntity.ok(workloadService.getMyWorkloads());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<WorkloadResponse> getWorkload(@PathVariable Long id) {
        return ResponseEntity.ok(workloadService.getWorkload(id));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<WorkloadResponse> cancelWorkload(@PathVariable Long id) {
        return ResponseEntity.ok(workloadService.cancelWorkload(id));
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<WorkloadResponse> completeWorkload(@PathVariable Long id) {
        return ResponseEntity.ok(workloadService.completeWorkload(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<Void> deleteWorkload(@PathVariable Long id) {
        workloadService.deleteWorkload(id);
        return ResponseEntity.noContent().build();
    }
}
