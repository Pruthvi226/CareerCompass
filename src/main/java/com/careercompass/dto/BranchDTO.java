package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.*;

/**
 * DTO for Branch/Course information
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDTO {

    private Long id;

    @NotBlank(message = "Branch name is required")
    @Size(min = 2, max = 100)
    private String name;

    @NotBlank(message = "Branch code is required")
    @Size(min = 2, max = 10)
    private String branchCode;

    private String degreeType;

    @Positive(message = "Duration must be positive")
    private Integer duration;

    private String description;

    private Boolean isActive;
}
