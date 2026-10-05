package com.skipps.gpu_farm_manager.gpu;

import com.skipps.gpu_farm_manager.gpunode.GpuNodeModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table (name = "gpus")
public class GpuModel
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "node_id", nullable = false)
    private GpuNodeModel gpuNode;

    @Column(name = "vendor", nullable = false, length = 100)
    private String vendor;

    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "architecture", length = 100)
    private String architecture;

    @Column(name = "vram_capacity", nullable = false)
    private Long vramCapacity;

    @Column(name = "compute_capability", length = 50)
    private String computeCapability;

    @Column(name = "status", nullable = false, length = 50)
    @Enumerated(EnumType.STRING)
    private GpuStatus status;
}
