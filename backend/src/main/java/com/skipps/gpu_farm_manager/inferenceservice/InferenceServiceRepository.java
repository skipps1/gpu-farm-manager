package com.skipps.gpu_farm_manager.inferenceservice;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface InferenceServiceRepository extends JpaRepository<InferenceServiceModel, Long> {

    @Query("SELECT s FROM InferenceServiceModel s JOIN FETCH s.model JOIN FETCH s.gpu g JOIN FETCH g.gpuNode WHERE s.id = :id")
    Optional<InferenceServiceModel> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT s FROM InferenceServiceModel s JOIN FETCH s.model JOIN FETCH s.gpu g JOIN FETCH g.gpuNode")
    List<InferenceServiceModel> findAllWithDetails();

    Optional<InferenceServiceModel> findByName(String name);

    boolean existsByName(String name);

    List<InferenceServiceModel> findAllByGpuId(Long gpuId);

    List<InferenceServiceModel> findAllByModelId(Long modelId);

    List<InferenceServiceModel> findAllByStatus(InferenceServiceStatus status);

    @Query("SELECT COALESCE(SUM(s.allocatedVram), 0) FROM InferenceServiceModel s WHERE s.gpu.id = :gpuId AND s.status IN ('STARTING', 'RUNNING')")
    Long sumAllocatedVramByGpuId(@Param("gpuId") Long gpuId);
}
