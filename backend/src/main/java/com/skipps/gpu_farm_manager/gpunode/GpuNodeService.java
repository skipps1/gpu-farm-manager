package com.skipps.gpu_farm_manager.gpunode;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.exception.GpuNodeAlreadyExistsException;
import com.skipps.gpu_farm_manager.exception.GpuNodeNotFoundException;
import com.skipps.gpu_farm_manager.gpunode.dto.CreateGpuNodeRequest;
import com.skipps.gpu_farm_manager.gpunode.dto.GpuNodeResponse;
import com.skipps.gpu_farm_manager.gpunode.dto.UpdateGpuNodeRequest;

@Service
@Transactional
public class GpuNodeService
{
    private final GpuNodeRepository repository;

    public GpuNodeService(GpuNodeRepository repository)
    {
        this.repository = repository;
    }

    public GpuNodeResponse addGpuNode(CreateGpuNodeRequest request)
    {
        if (repository.existsByHostname(request.hostName()))
        {
            throw GpuNodeAlreadyExistsException.withHostname(request.hostName());
        }

        GpuNodeModel newGpuNode = new GpuNodeModel();
        newGpuNode.setName(request.name());
        newGpuNode.setHostname(request.hostName());
        newGpuNode.setStatus(request.status() != null ? request.status() : GpuNodeStatus.OFFLINE);
        newGpuNode.setAgentVersion(request.agentVersion());
        newGpuNode.setCreatedAt(LocalDateTime.now());
        newGpuNode = repository.save(newGpuNode);

        return mapToResponse(newGpuNode);
    }

    @Transactional(readOnly = true)
    public GpuNodeResponse getGpuNode(String hostname)
    {
        GpuNodeModel gpuNode = repository.findByHostname(hostname)
            .orElseThrow(() -> GpuNodeNotFoundException.withHostname(hostname));

        return mapToResponse(gpuNode);
    }

    @Transactional(readOnly = true)
    public List<GpuNodeResponse> getAllGpuNodes()
    {
        List<GpuNodeModel> gpuNodes = repository.findAll();

        return gpuNodes.stream()
            .map(this::mapToResponse)
            .toList();
    }

    public GpuNodeResponse updateGpuNode(String hostname, UpdateGpuNodeRequest request)
    {
        GpuNodeModel gpuNode = repository.findByHostname(hostname)
            .orElseThrow(() -> GpuNodeNotFoundException.withHostname(hostname));

        if (request.hostName() != null && !request.hostName().equals(gpuNode.getHostname()) && repository.existsByHostname(request.hostName()))
        {
            throw GpuNodeAlreadyExistsException.withHostname(request.hostName());
        }

        if (request.name() != null)
        {
            gpuNode.setName(request.name());
        }
        if (request.hostName() != null)
        {
            gpuNode.setHostname(request.hostName());
        }
        if (request.agentVersion() != null)
        {
            gpuNode.setAgentVersion(request.agentVersion());
        }

        gpuNode = repository.save(gpuNode);

        return mapToResponse(gpuNode);
    }

    public void deleteGpuNode(String hostname)
    {
        GpuNodeModel gpuNode = repository.findByHostname(hostname)
            .orElseThrow(() -> GpuNodeNotFoundException.withHostname(hostname));

        repository.delete(gpuNode);
    }

    private GpuNodeResponse mapToResponse(GpuNodeModel node)
    {
        return new GpuNodeResponse(
            node.getId(),
            node.getName(),
            node.getHostname(),
            node.getStatus().toString(),
            node.getAgentVersion(),
            node.getCreatedAt()
        );
    }
}
