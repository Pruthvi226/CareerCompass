package com.careercompass.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for Wishlist items
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WishlistDTO {

    private Long id;

    private Long userId;

    private Long collegeId;

    private String collegeName;

    private String collegeType;

    private String city;

    private String state;

    private Long branchId;

    private String branchName;

    private String notes;

    private String createdAt;
}
