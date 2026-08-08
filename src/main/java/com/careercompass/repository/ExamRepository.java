package com.careercompass.repository;

import com.careercompass.entity.Exam;
import com.careercompass.entity.ExamType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.List;

/**
 * Repository for Exam entity
 */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    Optional<Exam> findByExamType(ExamType examType);

    List<Exam> findByIsActive(Boolean isActive);
}
