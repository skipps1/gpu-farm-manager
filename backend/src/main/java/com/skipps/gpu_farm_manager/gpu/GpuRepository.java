package com.skipps.gpu_farm_manager.gpu;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface GpuRepository extends JpaRepository<GpuModel, Long>
{
    @Query("SELECT g FROM GpuModel g JOIN FETCH g.gpuNode WHERE g.id = :id")
    Optional<GpuModel> findByIdWithNode(@Param("id") Long id);

    @Query("SELECT g FROM GpuModel g JOIN FETCH g.gpuNode")
    List<GpuModel> findAllWithNode();

    @Query("SELECT g FROM GpuModel g JOIN FETCH g.gpuNode WHERE g.gpuNode.hostname = :hostname")
    List<GpuModel> findAllByGpuNodeHostname(@Param("hostname") String hostname);
}
