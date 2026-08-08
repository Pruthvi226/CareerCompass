package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

/**
 * DTO for Prediction Results
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PredictionResultDTO {

    private Long id;

    private Long collegeId;

    private String collegeName;

    private String collegeType;

    private String counsellingType;

    private String city;

    private String state;

    private Long branchId;

    private String branchName;

    private String branchCode;

    private Integer previousClosingRank;

    private Integer studentRank;

    private Double chancePercentage;

    private String predictionType; // SAFE, MODERATE, DREAM

    private BigDecimal fees;

    private BigDecimal averagePackage;

    private BigDecimal highestPackage;

    private Integer placementPercentage;

    private Integer nirfRank;

    private Integer seatIntake;

    private String bondDetails;

    private String stipendDetails;

    private Boolean hospitalAttached;

    private String advice;

    private Integer rankDifference;

    private Boolean inWishlist;

    private Integer year;
}
