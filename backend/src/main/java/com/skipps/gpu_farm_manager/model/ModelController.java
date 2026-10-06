package com.skipps.gpu_farm_manager.model;

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

import com.skipps.gpu_farm_manager.model.dto.CreateModelRequest;
import com.skipps.gpu_farm_manager.model.dto.ModelResponse;
import com.skipps.gpu_farm_manager.model.dto.UpdateModelRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/models")
public class ModelController {

    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<ModelResponse> createModel(@RequestBody @Valid CreateModelRequest request) {
        return ResponseEntity.ok(modelService.createModel(request));
    }

    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        return ResponseEntity.ok(modelService.getAllModels());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModelResponse> getModel(@PathVariable Long id) {
        return ResponseEntity.ok(modelService.getModel(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<ModelResponse> updateModel(@PathVariable Long id, @RequestBody @Valid UpdateModelRequest request) {
        return ResponseEntity.ok(modelService.updateModel(id, request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        modelService.deleteModel(id);
        return ResponseEntity.noContent().build();
    }
}
