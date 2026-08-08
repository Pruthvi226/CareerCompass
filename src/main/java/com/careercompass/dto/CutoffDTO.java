package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Cutoff information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CutoffDTO {

    private Long id;

    private Long examId;

    private String examName;

    private Long collegeId;

    private String collegeName;

    private Long branchId;

    private String branchName;

    private String category;

    private String gender;

    private String quota;

    private Integer roundNumber;

    private Integer openingRank;

    private Integer closingRank;

    private Integer year;
}
