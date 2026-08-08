package com.careercompass.service;

import com.careercompass.entity.Exam;
import java.util.List;
import java.util.Optional;

/**
 * Exam service interface
 */
public interface ExamService {

    Exam addExam(Exam exam);

    Optional<Exam> getExamById(Long id);

    List<Exam> getAllExams();

    List<Exam> getActiveExams();

    Exam updateExam(Long id, Exam exam);

    void deleteExam(Long id);

    Optional<Exam> findByExamType(String examType);

    Long getTotalExams();
}
