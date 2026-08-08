package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Exam
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExamDTO {

    private Long id;

    private String examType;

    private String description;

    private String counsellingBody;

    private Boolean isActive;
}
