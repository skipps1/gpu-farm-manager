package com.skipps.gpu_farm_manager.gpu;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.GpuNodeNotFoundException;
import com.skipps.gpu_farm_manager.exception.GpuNotFoundException;
import com.skipps.gpu_farm_manager.gpu.dto.CreateGpuRequest;
import com.skipps.gpu_farm_manager.gpu.dto.GpuResponse;
import com.skipps.gpu_farm_manager.gpu.dto.UpdateGpuRequest;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeModel;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeRepository;

@Service
@Transactional
public class GpuService
{
	private final GpuRepository gpuRepository;
	private final GpuNodeRepository gpuNodeRepository;

	public GpuService(GpuRepository gpuRepository, GpuNodeRepository gpuNodeRepository)
	{
	    this.gpuRepository = gpuRepository;
		this.gpuNodeRepository = gpuNodeRepository;
	}

	public GpuResponse addGpu(CreateGpuRequest request)
	{
		GpuNodeModel gpuNode = gpuNodeRepository.findByHostname(request.gpuNodeHostname())
		    .orElseThrow(() -> GpuNodeNotFoundException.withHostname(request.gpuNodeHostname()));

	    GpuModel newGpu = new GpuModel();
		newGpu.setGpuNode(gpuNode);
		newGpu.setVendor(request.vendor());
		newGpu.setModel(request.model());
		newGpu.setArchitecture(request.architecture());
		newGpu.setVramCapacity(request.vramCapacity());
		newGpu.setComputeCapability(request.computeCapability());
		newGpu.setStatus(GpuStatus.AVAILABLE);
		newGpu = gpuRepository.save(newGpu);

		return mapToResponse(newGpu);
	}

	@Transactional(readOnly = true)
	public GpuResponse getGpu(Long id)
	{
	    GpuModel gpu = gpuRepository.findByIdWithNode(id)
			.orElseThrow(() -> new GpuNotFoundException(id));
		return mapToResponse(gpu);
	}

	@Transactional(readOnly = true)
	public List<GpuResponse> getAllGpusByGpuNode(String gpuNodeHostname)
	{
		if (!gpuNodeRepository.existsByHostname(gpuNodeHostname))
		{
			throw GpuNodeNotFoundException.withHostname(gpuNodeHostname);
		}

		List<GpuModel> gpus = gpuRepository.findAllByGpuNodeHostname(gpuNodeHostname);

		return gpus.stream()
			.map(this::mapToResponse)
			.toList();
	}

	public GpuResponse updateGpu(Long id, UpdateGpuRequest request)
	{
	    GpuModel gpu = gpuRepository.findByIdWithNode(id)
			.orElseThrow(() -> new GpuNotFoundException(id));

		if (request.vendor() != null)
		{
			gpu.setVendor(request.vendor());
		}
		if (request.model() != null)
		{
			gpu.setModel(request.model());
		}
		if (request.architecture() != null)
		{
			gpu.setArchitecture(request.architecture());
		}
		if (request.vramCapacity() != null)
		{
			gpu.setVramCapacity(request.vramCapacity());
		}
		if (request.computeCapability() != null)
		{
			gpu.setComputeCapability(request.computeCapability());
		}

		gpu = gpuRepository.save(gpu);

		return mapToResponse(gpu);
	}

	public void deleteGpu(Long id)
	{
        if (!gpuRepository.existsById(id))
        {
            throw new GpuNotFoundException(id);
        }
	    gpuRepository.deleteById(id);
	}

	private GpuResponse mapToResponse(GpuModel gpu)
	{
		return new GpuResponse(
		    gpu.getId(),
			gpu.getGpuNode() != null ? gpu.getGpuNode().getHostname() : null,
			gpu.getVendor(),
			gpu.getModel(),
			gpu.getArchitecture(),
			gpu.getVramCapacity(),
			gpu.getComputeCapability(),
			gpu.getStatus() != null ? gpu.getStatus().toString() : null
		);
	}
}
