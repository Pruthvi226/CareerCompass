package com.careercompass.service.impl;

import com.careercompass.dto.WishlistDTO;
import com.careercompass.entity.College;
import com.careercompass.entity.User;
import com.careercompass.entity.Wishlist;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.exception.UnauthorizedAccessException;
import com.careercompass.repository.CollegeRepository;
import com.careercompass.repository.BranchRepository;
import com.careercompass.repository.UserRepository;
import com.careercompass.repository.WishlistRepository;
import com.careercompass.service.WishlistService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of WishlistService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final CollegeRepository collegeRepository;
    private final BranchRepository branchRepository;

    @Override
    public WishlistDTO addToWishlist(Long userId, Long collegeId, Long branchId, String notes) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new ResourceNotFoundException("College not found"));

        Optional<Wishlist> existingWishlist = branchId == null
                ? wishlistRepository.findByUserIdAndCollegeId(userId, collegeId)
                : wishlistRepository.findByUserIdAndCollegeIdAndBranchId(userId, collegeId, branchId);
        if (existingWishlist.isPresent()) {
            Wishlist existing = existingWishlist.get();
            if (notes != null && !notes.isBlank()) {
                existing.setNotes(notes);
                return mapToDTO(wishlistRepository.save(existing));
            }
            return mapToDTO(existing);
        }

        Wishlist wishlist = Wishlist.builder()
                .user(user)
                .college(college)
                .branch(branchId != null ? branchRepository.findById(branchId).orElse(null) : null)
                .notes(notes)
                .build();

        Wishlist savedWishlist = wishlistRepository.save(wishlist);
        return mapToDTO(savedWishlist);
    }

    @Override
    public void removeFromWishlist(Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    public void removeWishlistItem(Long userId, Long wishlistId) {
        Wishlist wishlist = wishlistRepository.findByIdAndUserId(wishlistId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlistRepository.delete(wishlist);
    }

    @Override
    public void removeFromWishlist(Long userId, Long collegeId) {
        Optional<Wishlist> wishlist = wishlistRepository.findByUserIdAndCollegeId(userId, collegeId);
        wishlist.ifPresent(wishlistRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<WishlistDTO> getWishlistItem(Long wishlistId) {
        return wishlistRepository.findById(wishlistId).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<WishlistDTO> getUserWishlist(Long userId) {
        return wishlistRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long collegeId) {
        return wishlistRepository.findByUserIdAndCollegeId(userId, collegeId).isPresent();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isInWishlist(Long userId, Long collegeId, Long branchId) {
        return wishlistRepository.existsByUserIdAndCollegeIdAndBranchId(userId, collegeId, branchId);
    }

    @Override
    public WishlistDTO updateNotes(Long wishlistId, String notes) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId)
                .orElseThrow(() -> new ResourceNotFoundException("Wishlist item not found"));
        wishlist.setNotes(notes);
        Wishlist updatedWishlist = wishlistRepository.save(wishlist);
        return mapToDTO(updatedWishlist);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getWishlistCount(Long userId) {
        return wishlistRepository.countByUserId(userId);
    }

    @Override
    public void clearWishlist(Long userId) {
        List<Wishlist> wishlistItems = wishlistRepository.findByUserId(userId);
        wishlistRepository.deleteAll(wishlistItems);
    }

    private WishlistDTO mapToDTO(Wishlist wishlist) {
        return WishlistDTO.builder()
                .id(wishlist.getId())
                .userId(wishlist.getUser().getId())
                .collegeId(wishlist.getCollege().getId())
                .collegeName(wishlist.getCollege().getName())
                .collegeType(wishlist.getCollege().getCollegeType().toString())
                .city(wishlist.getCollege().getCity())
                .state(wishlist.getCollege().getState())
                .branchId(wishlist.getBranch() != null ? wishlist.getBranch().getId() : null)
                .branchName(wishlist.getBranch() != null ? wishlist.getBranch().getName() : null)
                .notes(wishlist.getNotes())
                .createdAt(wishlist.getCreatedAt().toString())
                .build();
    }
}
