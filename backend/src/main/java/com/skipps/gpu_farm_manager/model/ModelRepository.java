package com.skipps.gpu_farm_manager.model;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<ModelModel, Long> {
    Optional<ModelModel> findByNameAndVersion(String name, String version);
    boolean existsByNameAndVersion(String name, String version);
    List<ModelModel> findByName(String name);
}
