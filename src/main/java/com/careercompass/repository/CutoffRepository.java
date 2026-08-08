package com.careercompass.repository;

import com.careercompass.entity.Cutoff;
import com.careercompass.entity.Category;
import com.careercompass.entity.Gender;
import com.careercompass.entity.Quota;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for Cutoff entity
 */
@Repository
public interface CutoffRepository extends JpaRepository<Cutoff, Long> {

    List<Cutoff> findByExamIdAndCategoryAndGenderAndQuota(
            Long examId, Category category, Gender gender, Quota quota);

    List<Cutoff> findByExamIdAndCollegeIdAndBranchIdAndCategoryAndGenderAndQuota(
            Long examId, Long collegeId, Long branchId, Category category, Gender gender, Quota quota);

    List<Cutoff> findByCollegeId(Long collegeId);

    List<Cutoff> findByBranchId(Long branchId);

    List<Cutoff> findByExamId(Long examId);

    List<Cutoff> findByYearOrderByYearDesc(Integer year);

    @Query("SELECT c FROM Cutoff c WHERE c.exam.id = :examId AND c.category = :category " +
            "AND c.gender IN :genders AND c.quota = :quota ORDER BY c.college.nirfRank ASC NULLS LAST")
    List<Cutoff> findRelevantCutoffs(
            @Param("examId") Long examId,
            @Param("category") Category category,
            @Param("genders") List<Gender> genders,
            @Param("quota") Quota quota);

    @Query("SELECT c FROM Cutoff c WHERE c.exam.id = :examId AND c.college.id = :collegeId " +
            "AND c.category = :category AND c.gender = :gender AND c.quota = :quota " +
            "ORDER BY c.year DESC, c.roundNumber DESC")
    List<Cutoff> findHistoricalCutoffs(
            @Param("examId") Long examId,
            @Param("collegeId") Long collegeId,
            @Param("category") Category category,
            @Param("gender") Gender gender,
            @Param("quota") Quota quota);

    List<Cutoff> findByExamIdAndBranchIdAndCategoryAndGenderAndQuota(
            Long examId, Long branchId, Category category, Gender gender, Quota quota);

    boolean existsByExamIdAndCollegeIdAndBranchIdAndCategoryAndGenderAndQuotaAndRoundNumberAndYear(
            Long examId, Long collegeId, Long branchId, Category category, Gender gender, Quota quota,
            Integer roundNumber, Integer year);
}
