package com.careercompass.service;

import com.careercompass.dto.CutoffDTO;
import com.careercompass.entity.Category;
import com.careercompass.entity.Gender;
import com.careercompass.entity.Quota;
import java.util.List;
import java.util.Optional;

/**
 * Cutoff service interface
 */
public interface CutoffService {

    CutoffDTO addCutoff(CutoffDTO cutoffDTO);

    Optional<CutoffDTO> getCutoffById(Long id);

    List<CutoffDTO> getCutoffsByCollege(Long collegeId);

    List<CutoffDTO> getCutoffsByExam(Long examId);

    List<CutoffDTO> getRelevantCutoffs(Long examId, Category category, Gender gender, Quota quota);

    List<CutoffDTO> getHistoricalCutoffs(Long examId, Long collegeId, Category category, Gender gender, Quota quota);

    List<CutoffDTO> getCutoffsByBranch(Long branchId);

    CutoffDTO updateCutoff(Long id, CutoffDTO cutoffDTO);

    void deleteCutoff(Long id);

    Long getTotalCutoffRecords();

    List<CutoffDTO> getCutoffsByYear(Integer year);
}
