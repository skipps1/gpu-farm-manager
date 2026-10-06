package com.skipps.gpu_farm_manager.inferenceservice;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.gpu_farm_manager.inferenceservice.dto.CreateInferenceServiceRequest;
import com.skipps.gpu_farm_manager.inferenceservice.dto.InferenceServiceResponse;
import com.skipps.gpu_farm_manager.inferenceservice.dto.UpdateInferenceServiceRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/services")
public class InferenceServiceController {

    private final InferenceServiceService service;

    public InferenceServiceController(InferenceServiceService service) {
        this.service = service;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<InferenceServiceResponse> createService(@RequestBody @Valid CreateInferenceServiceRequest request) {
        return ResponseEntity.ok(service.createService(request));
    }

    @GetMapping
    public ResponseEntity<List<InferenceServiceResponse>> getAllServices() {
        return ResponseEntity.ok(service.getAllServices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InferenceServiceResponse> getService(@PathVariable Long id) {
        return ResponseEntity.ok(service.getService(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<InferenceServiceResponse> updateService(
        @PathVariable Long id,
        @RequestBody @Valid UpdateInferenceServiceRequest request
    ) {
        return ResponseEntity.ok(service.updateService(id, request));
    }

    @PostMapping("/{id}/start")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<InferenceServiceResponse> startService(@PathVariable Long id) {
        return ResponseEntity.ok(service.startService(id));
    }

    @PostMapping("/{id}/stop")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<InferenceServiceResponse> stopService(@PathVariable Long id) {
        return ResponseEntity.ok(service.stopService(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<Void> deleteService(@PathVariable Long id) {
        service.deleteService(id);
        return ResponseEntity.noContent().build();
    }
}
