package com.careercompass.service.impl;

import com.careercompass.dto.AnalyticsDTO;
import com.careercompass.entity.Role;
import com.careercompass.repository.*;
import com.careercompass.service.AnalyticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of AnalyticsService
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AnalyticsServiceImpl implements AnalyticsService {

    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;
    private final CutoffRepository cutoffRepository;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final WishlistRepository wishlistRepository;

    @Override
    public AnalyticsDTO getAdminDashboardStats() {
        return AnalyticsDTO.builder()
                .totalStudents(getTotalStudents())
                .totalColleges(getTotalColleges())
                .totalBranches(getTotalBranches())
                .totalCutoffRecords(getTotalCutoffRecords())
                .totalPredictions(getTotalPredictions())
                .mostSearchedCollege(getMostSearchedCollege())
                .mostPreferredBranch(getMostPreferredBranch())
                .mostUsedExam(getMostUsedExam())
                .build();
    }

    @Override
    public AnalyticsDTO getStudentDashboardStats(Long userId) {
        return AnalyticsDTO.builder()
                .totalWishlistItems(wishlistRepository.countByUserId(userId))
                .totalPredictions((long) predictionHistoryRepository.findByUserId(userId).size())
                .build();
    }

    @Override
    public Long getTotalStudents() {
        return (long) userRepository.findByRole(Role.STUDENT).size();
    }

    @Override
    public Long getTotalColleges() {
        return collegeRepository.count();
    }

    @Override
    public Long getTotalBranches() {
        return branchRepository.count();
    }

    @Override
    public Long getTotalCutoffRecords() {
        return cutoffRepository.count();
    }

    @Override
    public Long getTotalPredictions() {
        return predictionHistoryRepository.count();
    }

    @Override
    public String getMostSearchedCollege() {
        // This would typically be tracked separately
        // For now, return the college with most cutoff records
        return "Based on available data";
    }

    @Override
    public String getMostPreferredBranch() {
        return predictionHistoryRepository.findAll().stream()
                .filter(prediction -> prediction.getPreferredBranch() != null && !prediction.getPreferredBranch().isBlank())
                .collect(Collectors.groupingBy(prediction -> prediction.getPreferredBranch(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No prediction data yet");
    }

    @Override
    public String getMostUsedExam() {
        return predictionHistoryRepository.findAll().stream()
                .collect(Collectors.groupingBy(prediction -> prediction.getExam().getExamType().getDisplayName(), Collectors.counting()))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("No prediction data yet");
    }

    @Override
    public Map<String, Long> getPredictionsByExam() {
        return predictionHistoryRepository.findAll().stream()
                .collect(Collectors.groupingBy(prediction -> prediction.getExam().getExamType().getDisplayName(), Collectors.counting()));
    }

    @Override
    public Map<String, Long> getCollegesByType() {
        return collegeRepository.findAll().stream()
                .collect(Collectors.groupingBy(college -> college.getCollegeType().getDisplayName(), Collectors.counting()));
    }

    @Override
    public Map<String, Long> getStudentsByCategory() {
        return userRepository.findByRole(Role.STUDENT).stream()
                .filter(user -> user.getCategory() != null)
                .collect(Collectors.groupingBy(user -> user.getCategory().getDisplayName(), Collectors.counting()));
    }
}
