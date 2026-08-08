package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for College information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CollegeDTO {

    private Long id;

    @NotBlank(message = "College name is required")
    private String name;

    private String collegeType;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    private BigDecimal fees;

    private BigDecimal averagePackage;

    private BigDecimal highestPackage;

    private Integer placementPercentage;

    private Integer nirfRank;

    private String website;

    private Boolean hostelAvailable;

    private Integer campusSize;

    private String description;

    private String counsellingType;

    private Integer seatIntake;

    private String bondDetails;

    private String stipendDetails;

    private Boolean hospitalAttached;

    private String annualPatientFlow;

    private Boolean isActive;
}
