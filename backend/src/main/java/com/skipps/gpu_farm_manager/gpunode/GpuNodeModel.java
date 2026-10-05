package com.skipps.gpu_farm_manager.gpunode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.skipps.gpu_farm_manager.gpu.GpuModel;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gpu_nodes")
public class GpuNodeModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String hostname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GpuNodeStatus status;

    @Column(name = "agent_version")
    private String agentVersion;

    @Column(name = "created_at",nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @OneToMany (mappedBy = "gpuNode", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GpuModel> gpus = new ArrayList<>();

}
