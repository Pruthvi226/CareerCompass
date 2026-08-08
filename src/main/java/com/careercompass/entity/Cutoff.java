package com.careercompass.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * Cutoff entity storing historical cutoff data for counselling
 */
@Entity
@Table(name = "cutoffs", indexes = {
    @Index(name = "idx_exam_college", columnList = "exam_id,college_id"),
    @Index(name = "idx_year_round", columnList = "year,round_number"),
    @Index(name = "idx_closing_rank", columnList = "closing_rank")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cutoff {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "branch_id", nullable = false)
    private Branch branch;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false)
    private Category category;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "quota", nullable = false)
    private Quota quota;

    @Positive(message = "Round number must be positive")
    @Column(name = "round_number", nullable = false)
    private Integer roundNumber;

    @Positive(message = "Opening rank must be positive")
    @Column(name = "opening_rank", nullable = false)
    private Integer openingRank;

    @Positive(message = "Closing rank must be positive")
    @Column(name = "closing_rank", nullable = false)
    private Integer closingRank;

    @Min(value = 1950, message = "Year must be valid")
    @Max(value = 2100, message = "Year must be valid")
    @Column(name = "year", nullable = false)
    private Integer year;

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
