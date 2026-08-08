package com.careercompass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * College entity representing colleges participating in counselling
 */
@Entity
@Table(name = "colleges", indexes = {
    @Index(name = "idx_college_name", columnList = "name"),
    @Index(name = "idx_college_type", columnList = "college_type"),
    @Index(name = "idx_college_state", columnList = "state")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "College name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "college_type", nullable = false)
    private CollegeType collegeType;

    @NotBlank(message = "City is required")
    @Column(name = "city", nullable = false)
    private String city;

    @NotBlank(message = "State is required")
    @Column(name = "state", nullable = false)
    private String state;

    @Positive(message = "Fees must be positive")
    @Column(name = "fees")
    private BigDecimal fees;

    @Positive(message = "Average package must be positive")
    @Column(name = "average_package")
    private BigDecimal averagePackage;

    @Positive(message = "Highest package must be positive")
    @Column(name = "highest_package")
    private BigDecimal highestPackage;

    @Min(value = 0, message = "Placement percentage must be between 0 and 100")
    @Max(value = 100, message = "Placement percentage must be between 0 and 100")
    @Column(name = "placement_percentage")
    private Integer placementPercentage;

    @Positive(message = "NIRF rank must be positive")
    @Column(name = "nirf_rank")
    private Integer nirfRank;

    @Column(name = "website")
    private String website;

    @Column(name = "hostel_available")
    @Builder.Default
    private Boolean hostelAvailable = false;

    @Positive(message = "Campus size must be positive")
    @Column(name = "campus_size")
    private Integer campusSize;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "counselling_type")
    private String counsellingType;

    @Positive(message = "Seat intake must be positive")
    @Column(name = "seat_intake")
    private Integer seatIntake;

    @Column(name = "bond_details")
    private String bondDetails;

    @Column(name = "stipend_details")
    private String stipendDetails;

    @Column(name = "hospital_attached")
    @Builder.Default
    private Boolean hospitalAttached = false;

    @Column(name = "annual_patient_flow")
    private String annualPatientFlow;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
