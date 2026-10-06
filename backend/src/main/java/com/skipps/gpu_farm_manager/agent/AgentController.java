package com.skipps.gpu_farm_manager.agent;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.skipps.gpu_farm_manager.agent.dto.AgentHeartbeatRequest;
import com.skipps.gpu_farm_manager.agent.dto.AgentHeartbeatResponse;
import com.skipps.gpu_farm_manager.agent.dto.AgentRegisterRequest;
import com.skipps.gpu_farm_manager.agent.dto.AgentRegisterResponse;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/agent")
@PreAuthorize("hasAnyRole('ADMINISTRATOR', 'OPERATOR')")
public class AgentController {

    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/register")
    public ResponseEntity<AgentRegisterResponse> register(@RequestBody @Valid AgentRegisterRequest request) {
        return ResponseEntity.ok(agentService.register(request));
    }

    @PostMapping("/heartbeat")
    public ResponseEntity<AgentHeartbeatResponse> heartbeat(@RequestBody @Valid AgentHeartbeatRequest request) {
        return ResponseEntity.ok(agentService.heartbeat(request));
    }

    @GetMapping("/metrics/{hostname}")
    public ResponseEntity<AgentHeartbeatRequest> getMetrics(@PathVariable String hostname) {
        return ResponseEntity.ok(agentService.getLatestTelemetry(hostname));
    }
}
