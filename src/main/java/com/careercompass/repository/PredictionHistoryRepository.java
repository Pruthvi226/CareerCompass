package com.careercompass.repository;

import com.careercompass.entity.PredictionHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for PredictionHistory entity
 */
@Repository
public interface PredictionHistoryRepository extends JpaRepository<PredictionHistory, Long> {

    List<PredictionHistory> findByUserId(Long userId);

    List<PredictionHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<PredictionHistory> findByExamId(Long examId);

    @Query("SELECT p FROM PredictionHistory p WHERE p.user.id = :userId ORDER BY p.createdAt DESC")
    List<PredictionHistory> findUserPredictionsRecent(@Param("userId") Long userId);

    @Query("SELECT COUNT(p) FROM PredictionHistory p WHERE p.exam.id = :examId")
    Long countByExamId(@Param("examId") Long examId);

    @Query("SELECT COUNT(DISTINCT p.preferredBranch) FROM PredictionHistory p WHERE p.preferredBranch IS NOT NULL")
    Long countDistinctPreferredBranches();
}
