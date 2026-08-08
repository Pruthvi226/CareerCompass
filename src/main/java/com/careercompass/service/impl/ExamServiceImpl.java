package com.careercompass.service.impl;

import com.careercompass.dto.ExamDTO;
import com.careercompass.entity.Exam;
import com.careercompass.entity.ExamType;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.ExamRepository;
import com.careercompass.service.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ExamService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ExamServiceImpl implements ExamService {

    private final ExamRepository examRepository;

    @Override
    public Exam addExam(Exam exam) {
        return examRepository.save(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exam> getExamById(Long id) {
        return examRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getAllExams() {
        return examRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Exam> getActiveExams() {
        return examRepository.findByIsActive(true);
    }

    @Override
    public Exam updateExam(Long id, Exam exam) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + id));

        existingExam.setDescription(exam.getDescription());
        if (exam.getIsActive() != null) {
            existingExam.setIsActive(exam.getIsActive());
        }

        return examRepository.save(existingExam);
    }

    @Override
    public void deleteExam(Long id) {
        Exam exam = examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found with id " + id));
        examRepository.delete(exam);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Exam> findByExamType(String examType) {
        try {
            ExamType type = ExamType.valueOf(examType);
            return examRepository.findByExamType(type);
        } catch (IllegalArgumentException e) {
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalExams() {
        return examRepository.count();
    }
}
