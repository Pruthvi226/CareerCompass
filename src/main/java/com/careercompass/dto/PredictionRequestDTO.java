package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * DTO for prediction request from student
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionRequestDTO {

    @NotNull(message = "Exam is required")
    private Long examId;

    @Positive(message = "Rank must be positive")
    private Integer rank;

    @Min(value = 0, message = "Score cannot be negative")
    @Max(value = 1000, message = "Score looks too high")
    private Integer score;

    @NotBlank(message = "Category is required")
    private String category;

    @NotBlank(message = "Gender is required")
    private String gender;

    @NotBlank(message = "Quota is required")
    private String quota;

    private String homeState;

    private String preferredBranch;

    private String preferredCollegeType;

    private String statePreference;

    private java.math.BigDecimal maxFees;

    private Integer minClosingRank;

    private Integer maxClosingRank;
}
