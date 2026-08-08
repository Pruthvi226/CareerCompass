package com.careercompass.repository;

import com.careercompass.entity.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Wishlist entity
 */
@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Long> {

    List<Wishlist> findByUserId(Long userId);

    List<Wishlist> findByUserIdOrderByCreatedAtDesc(Long userId);

    Optional<Wishlist> findByUserIdAndCollegeId(Long userId, Long collegeId);

    Optional<Wishlist> findByUserIdAndCollegeIdAndBranchId(Long userId, Long collegeId, Long branchId);

    Optional<Wishlist> findByIdAndUserId(Long id, Long userId);

    boolean existsByUserIdAndCollegeIdAndBranchId(Long userId, Long collegeId, Long branchId);

    Long countByUserId(Long userId);

    @Query("SELECT w FROM Wishlist w WHERE w.user.id = :userId AND w.college.id = :collegeId")
    Optional<Wishlist> findWishlistItem(@Param("userId") Long userId, @Param("collegeId") Long collegeId);
}
