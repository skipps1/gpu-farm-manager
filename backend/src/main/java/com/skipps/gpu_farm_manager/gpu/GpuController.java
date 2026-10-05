package com.skipps.gpu_farm_manager.gpu;

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

import com.skipps.gpu_farm_manager.gpu.dto.CreateGpuRequest;
import com.skipps.gpu_farm_manager.gpu.dto.GpuResponse;
import com.skipps.gpu_farm_manager.gpu.dto.UpdateGpuRequest;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
public class GpuController
{
	private final GpuService service;

	public GpuController(GpuService service)
	{
	    this.service = service;
	}

	@PostMapping("/gpus")
	public ResponseEntity<GpuResponse> addGpu(@RequestBody @Valid CreateGpuRequest request)
	{
	    return ResponseEntity.ok(service.addGpu(request));
	}

	@GetMapping("/nodes/{hostname}/gpus")
	public ResponseEntity<List<GpuResponse>> getGpus(@PathVariable String hostname)
	{
	    return ResponseEntity.ok(service.getAllGpusByGpuNode(hostname));
	}

	@GetMapping("/gpus/{id}")
	public ResponseEntity<GpuResponse> getGpu(@PathVariable Long id)
	{
	    return ResponseEntity.ok(service.getGpu(id));
	}

	@PutMapping("/gpus/{id}")
	public ResponseEntity<GpuResponse> updateGpu(@PathVariable Long id, @RequestBody UpdateGpuRequest request)
	{
	    return ResponseEntity.ok(service.updateGpu(id, request));
	}

	@DeleteMapping("/gpus/{id}")
	public ResponseEntity<Void> deleteGpu(@PathVariable Long id)
	{
	    service.deleteGpu(id);
	    return ResponseEntity.noContent().build();
	}
}
