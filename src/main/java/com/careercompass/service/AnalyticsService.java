package com.careercompass.service;

import com.careercompass.dto.AnalyticsDTO;
import java.util.Map;

/**
 * Analytics service interface
 */
public interface AnalyticsService {

    AnalyticsDTO getAdminDashboardStats();

    AnalyticsDTO getStudentDashboardStats(Long userId);

    Long getTotalStudents();

    Long getTotalColleges();

    Long getTotalBranches();

    Long getTotalCutoffRecords();

    Long getTotalPredictions();

    String getMostSearchedCollege();

    String getMostPreferredBranch();

    String getMostUsedExam();

    Map<String, Long> getPredictionsByExam();

    Map<String, Long> getCollegesByType();

    Map<String, Long> getStudentsByCategory();
}
