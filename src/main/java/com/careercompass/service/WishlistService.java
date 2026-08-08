package com.careercompass.service;

import com.careercompass.dto.WishlistDTO;
import java.util.List;
import java.util.Optional;

/**
 * Wishlist service interface
 */
public interface WishlistService {

    WishlistDTO addToWishlist(Long userId, Long collegeId, Long branchId, String notes);

    void removeFromWishlist(Long wishlistId);

    void removeWishlistItem(Long userId, Long wishlistId);

    void removeFromWishlist(Long userId, Long collegeId);

    Optional<WishlistDTO> getWishlistItem(Long wishlistId);

    List<WishlistDTO> getUserWishlist(Long userId);

    boolean isInWishlist(Long userId, Long collegeId);

    boolean isInWishlist(Long userId, Long collegeId, Long branchId);

    WishlistDTO updateNotes(Long wishlistId, String notes);

    Long getWishlistCount(Long userId);

    void clearWishlist(Long userId);
}
