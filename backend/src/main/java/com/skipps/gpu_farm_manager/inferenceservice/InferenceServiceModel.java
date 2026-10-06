package com.skipps.gpu_farm_manager.inferenceservice;

import com.skipps.gpu_farm_manager.gpu.GpuModel;
import com.skipps.gpu_farm_manager.model.ModelModel;

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
@Table(name = "inference_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class InferenceServiceModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, length = 100)
    private String backend;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "model_id", nullable = false)
    private ModelModel model;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gpu_id", nullable = false)
    private GpuModel gpu;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private InferenceServiceStatus status;

    @Column(name = "allocated_vram", nullable = false)
    private Long allocatedVram;

    @Column(name = "endpoint")
    private String endpoint;
}
