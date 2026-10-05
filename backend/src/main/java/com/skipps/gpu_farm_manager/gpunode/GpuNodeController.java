package com.skipps.gpu_farm_manager.gpunode;

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

import com.skipps.gpu_farm_manager.gpunode.dto.CreateGpuNodeRequest;
import com.skipps.gpu_farm_manager.gpunode.dto.GpuNodeResponse;
import com.skipps.gpu_farm_manager.gpunode.dto.UpdateGpuNodeRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/nodes")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
public class GpuNodeController
{
    private final GpuNodeService service;

    public GpuNodeController(GpuNodeService service)
    {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<GpuNodeResponse> addGpuNode(@RequestBody @Valid CreateGpuNodeRequest request)
    {
        return ResponseEntity.ok(service.addGpuNode(request));
    }

    @GetMapping
    public ResponseEntity<List<GpuNodeResponse>> getGpuNodes()
    {
        return ResponseEntity.ok(service.getAllGpuNodes());
    }

    @GetMapping("/{hostname}")
    public ResponseEntity<GpuNodeResponse> getGpuNode(@PathVariable String hostname)
    {
        return ResponseEntity.ok(service.getGpuNode(hostname));
    }

    @PutMapping("/{hostname}")
    public ResponseEntity<GpuNodeResponse> updateGpuNode(@PathVariable String hostname, @RequestBody @Valid UpdateGpuNodeRequest request)
    {
        return ResponseEntity.ok(service.updateGpuNode(hostname, request));
    }

    @DeleteMapping("/{hostname}")
    public ResponseEntity<Void> deleteMapping(@PathVariable String hostname)
    {
        service.deleteGpuNode(hostname);
        return ResponseEntity.noContent().build();
    }
}
