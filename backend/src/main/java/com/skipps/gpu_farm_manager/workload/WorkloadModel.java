package com.skipps.gpu_farm_manager.workload;

import java.time.LocalDateTime;

import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.inferenceservice.InferenceServiceModel;
import com.skipps.gpu_farm_manager.model.ModelModel;
import com.skipps.gpu_farm_manager.user.UserModel;

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
@Table(name = "workloads")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id")
    private InferenceServiceModel service;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpu_id")
    private GpuModel gpu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WorkloadStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private WorkloadPriority priority;

    @Column(name = "requested_vram", nullable = false)
    private Long requestedVram;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "finished_at")
    private LocalDateTime finishedAt;
}
