package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Analytics dashboard
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyticsDTO {

    private Long totalStudents;

    private Long totalColleges;

    private Long totalCutoffRecords;

    private Long totalPredictions;

    private Long totalWishlistItems;

    private Long totalBranches;

    private String mostSearchedCollege;

    private String mostPreferredBranch;

    private String mostUsedExam;
}
