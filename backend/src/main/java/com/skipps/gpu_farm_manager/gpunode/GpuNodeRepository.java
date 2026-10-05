package com.skipps.gpu_farm_manager.gpunode;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GpuNodeRepository extends JpaRepository<GpuNodeModel, Long>
{
    boolean existsByHostname(String hostname);
    Optional<GpuNodeModel> findByHostname(String hostname);
    void deleteByHostname(String hostname);
}
