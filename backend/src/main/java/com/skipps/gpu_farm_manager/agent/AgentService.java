package com.skipps.gpu_farm_manager.agent;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.skipps.gpu_farm_manager.agent.dto.AgentGpuRegisterDto;
import com.skipps.gpu_farm_manager.agent.dto.AgentHeartbeatRequest;
import com.skipps.gpu_farm_manager.agent.dto.AgentHeartbeatResponse;
import com.skipps.gpu_farm_manager.agent.dto.AgentRegisterRequest;
import com.skipps.gpu_farm_manager.agent.dto.AgentRegisterResponse;
import com.skipps.gpu_farm_manager.exception.GpuNodeNotFoundException;
import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.gpu.GpuRepository;
import com.skipps.gpu_farm_manager.gpu.GpuStatus;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeModel;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeRepository;
import com.skipps.gpu_farm_manager.gpunode.GpuNodeStatus;

@Service
@Transactional
public class AgentService {

    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final GpuNodeRepository nodeRepository;
    private final GpuRepository gpuRepository;
    private final Map<String, AgentHeartbeatRequest> latestTelemetry = new ConcurrentHashMap<>();

    public AgentService(GpuNodeRepository nodeRepository, GpuRepository gpuRepository) {
        this.nodeRepository = nodeRepository;
        this.gpuRepository = gpuRepository;
    }

    public AgentRegisterResponse register(AgentRegisterRequest request) {
        log.info("Agent registration request from hostname: {}", request.hostname());

        GpuNodeModel node = nodeRepository.findByHostname(request.hostname())
            .orElseGet(() -> {
                GpuNodeModel newNode = new GpuNodeModel();
                newNode.setHostname(request.hostname());
                newNode.setName(request.name() != null && !request.name().isBlank() ? request.name() : request.hostname());
                newNode.setCreatedAt(LocalDateTime.now());
                return newNode;
            });

        node.setStatus(GpuNodeStatus.ONLINE);
        if (request.name() != null && !request.name().isBlank()) {
            node.setName(request.name());
        }
        if (request.agentVersion() != null) {
            node.setAgentVersion(request.agentVersion());
        }

        node = nodeRepository.save(node);

        List<GpuModel> existingGpus = gpuRepository.findAllByGpuNodeHostname(node.getHostname());
        int registeredCount = 0;

        if (existingGpus.isEmpty()) {
            for (AgentGpuRegisterDto gpuDto : request.gpus()) {
                GpuModel gpu = new GpuModel();
                gpu.setGpuNode(node);
                gpu.setVendor(gpuDto.vendor());
                gpu.setModel(gpuDto.model());
                gpu.setArchitecture(gpuDto.architecture());
                gpu.setVramCapacity(gpuDto.vramCapacity());
                gpu.setComputeCapability(gpuDto.computeCapability());
                gpu.setStatus(GpuStatus.AVAILABLE);
                gpuRepository.save(gpu);
                registeredCount++;
            }
        } else {
            registeredCount = existingGpus.size();
            for (GpuModel gpu : existingGpus) {
                if (gpu.getStatus() == GpuStatus.ERROR) {
                    gpu.setStatus(GpuStatus.AVAILABLE);
                    gpuRepository.save(gpu);
                }
            }
        }

        log.info("Node '{}' registered successfully with {} GPUs", node.getHostname(), registeredCount);

        return new AgentRegisterResponse(
            node.getId(),
            node.getHostname(),
            node.getStatus().toString(),
            registeredCount,
            String.format("Node '%s' registered successfully with %d GPU(s)", node.getHostname(), registeredCount),
            LocalDateTime.now()
        );
    }

    public AgentHeartbeatResponse heartbeat(AgentHeartbeatRequest request) {
        GpuNodeModel node = nodeRepository.findByHostname(request.hostname())
            .orElseThrow(() -> GpuNodeNotFoundException.withHostname(request.hostname()));

        node.setStatus(GpuNodeStatus.ONLINE);
        if (request.agentVersion() != null) {
            node.setAgentVersion(request.agentVersion());
        }
        nodeRepository.save(node);

        latestTelemetry.put(request.hostname(), request);

        log.debug("Heartbeat received from node '{}' with {} GPU telemetry records",
            request.hostname(),
            request.gpus() != null ? request.gpus().size() : 0);

        return new AgentHeartbeatResponse(
            request.hostname(),
            "OK",
            LocalDateTime.now()
        );
    }

    @Transactional(readOnly = true)
    public AgentHeartbeatRequest getLatestTelemetry(String hostname) {
        if (!nodeRepository.existsByHostname(hostname)) {
            throw GpuNodeNotFoundException.withHostname(hostname);
        }
        return latestTelemetry.get(hostname);
    }
}
