package com.skipps.gpu_farm_manager.workload;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkloadRepository extends JpaRepository<WorkloadModel, Long> {

    @Query("SELECT w FROM WorkloadModel w JOIN FETCH w.user JOIN FETCH w.model LEFT JOIN FETCH w.service LEFT JOIN FETCH w.gpu WHERE w.id = :id")
    Optional<WorkloadModel> findByIdWithDetails(@Param("id") Long id);

    @Query("SELECT w FROM WorkloadModel w JOIN FETCH w.user JOIN FETCH w.model LEFT JOIN FETCH w.service LEFT JOIN FETCH w.gpu")
    List<WorkloadModel> findAllWithDetails();

    @Query("SELECT w FROM WorkloadModel w JOIN FETCH w.user JOIN FETCH w.model LEFT JOIN FETCH w.service LEFT JOIN FETCH w.gpu WHERE w.user.id = :userId")
    List<WorkloadModel> findAllByUserIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT w FROM WorkloadModel w JOIN FETCH w.user JOIN FETCH w.model LEFT JOIN FETCH w.service LEFT JOIN FETCH w.gpu WHERE w.user.username = :username")
    List<WorkloadModel> findAllByUsernameWithDetails(@Param("username") String username);

    List<WorkloadModel> findAllByStatus(WorkloadStatus status);

    List<WorkloadModel> findByStatusOrderByPriorityDescStartedAtAsc(WorkloadStatus status);

    long countByUserIdAndStatus(Long userId, WorkloadStatus status);

    @Query("SELECT COALESCE(SUM(w.requestedVram), 0) FROM WorkloadModel w WHERE w.gpu.id = :gpuId AND w.status = 'RUNNING'")
    Long sumRunningVramByGpuId(@Param("gpuId") Long gpuId);
}
