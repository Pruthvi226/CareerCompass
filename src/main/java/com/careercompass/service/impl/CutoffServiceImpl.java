package com.careercompass.service.impl;

import com.careercompass.dto.CutoffDTO;
import com.careercompass.entity.*;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.BranchRepository;
import com.careercompass.repository.CollegeRepository;
import com.careercompass.repository.CutoffRepository;
import com.careercompass.repository.ExamRepository;
import com.careercompass.service.CutoffService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CutoffService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CutoffServiceImpl implements CutoffService {

    private final CutoffRepository cutoffRepository;
    private final ExamRepository examRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;

    @Override
    public CutoffDTO addCutoff(CutoffDTO cutoffDTO) {
        Exam exam = examRepository.findById(cutoffDTO.getExamId())
                .orElseThrow(() -> new ResourceNotFoundException("Exam not found"));
        College college = collegeRepository.findById(cutoffDTO.getCollegeId())
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));
        Branch branch = branchRepository.findById(cutoffDTO.getBranchId())
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found"));

        Cutoff cutoff = Cutoff.builder()
                .exam(exam)
                .college(college)
                .branch(branch)
                .category(Category.valueOf(cutoffDTO.getCategory()))
                .gender(Gender.valueOf(cutoffDTO.getGender()))
                .quota(Quota.valueOf(cutoffDTO.getQuota()))
                .roundNumber(cutoffDTO.getRoundNumber())
                .openingRank(cutoffDTO.getOpeningRank())
                .closingRank(cutoffDTO.getClosingRank())
                .year(cutoffDTO.getYear())
                .build();

        Cutoff savedCutoff = cutoffRepository.save(cutoff);
        return mapToDTO(savedCutoff);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CutoffDTO> getCutoffById(Long id) {
        return cutoffRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getCutoffsByCollege(Long collegeId) {
        return cutoffRepository.findByCollegeId(collegeId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getCutoffsByExam(Long examId) {
        return cutoffRepository.findByExamId(examId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getRelevantCutoffs(Long examId, Category category, Gender gender, Quota quota) {
        List<Gender> acceptedGenders = gender == Gender.GENDER_NEUTRAL
                ? List.of(Gender.GENDER_NEUTRAL)
                : List.of(gender, Gender.GENDER_NEUTRAL);
        return cutoffRepository.findRelevantCutoffs(examId, category, acceptedGenders, quota).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getHistoricalCutoffs(Long examId, Long collegeId, Category category, Gender gender, Quota quota) {
        return cutoffRepository.findHistoricalCutoffs(examId, collegeId, category, gender, quota).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getCutoffsByBranch(Long branchId) {
        return cutoffRepository.findByBranchId(branchId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CutoffDTO updateCutoff(Long id, CutoffDTO cutoffDTO) {
        Cutoff cutoff = cutoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cutoff not found"));

        cutoff.setRoundNumber(cutoffDTO.getRoundNumber());
        cutoff.setOpeningRank(cutoffDTO.getOpeningRank());
        cutoff.setClosingRank(cutoffDTO.getClosingRank());

        Cutoff updatedCutoff = cutoffRepository.save(cutoff);
        return mapToDTO(updatedCutoff);
    }

    @Override
    public void deleteCutoff(Long id) {
        Cutoff cutoff = cutoffRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cutoff not found"));
        cutoffRepository.delete(cutoff);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalCutoffRecords() {
        return cutoffRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CutoffDTO> getCutoffsByYear(Integer year) {
        return cutoffRepository.findByYearOrderByYearDesc(year).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CutoffDTO mapToDTO(Cutoff cutoff) {
        return CutoffDTO.builder()
                .id(cutoff.getId())
                .examId(cutoff.getExam().getId())
                .examName(cutoff.getExam().getExamType().getDisplayName())
                .collegeId(cutoff.getCollege().getId())
                .collegeName(cutoff.getCollege().getName())
                .branchId(cutoff.getBranch().getId())
                .branchName(cutoff.getBranch().getName())
                .category(cutoff.getCategory().toString())
                .gender(cutoff.getGender().toString())
                .quota(cutoff.getQuota().toString())
                .roundNumber(cutoff.getRoundNumber())
                .openingRank(cutoff.getOpeningRank())
                .closingRank(cutoff.getClosingRank())
                .year(cutoff.getYear())
                .build();
    }
}
