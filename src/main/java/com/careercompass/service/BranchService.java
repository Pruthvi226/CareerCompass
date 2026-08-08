package com.careercompass.service;

import com.careercompass.dto.BranchDTO;
import java.util.List;
import java.util.Optional;

/**
 * Branch service interface
 */
public interface BranchService {

    BranchDTO addBranch(BranchDTO branchDTO);

    Optional<BranchDTO> getBranchById(Long id);

    List<BranchDTO> getAllBranches();

    BranchDTO updateBranch(Long id, BranchDTO branchDTO);

    void deleteBranch(Long id);

    List<BranchDTO> searchBranches(String keyword);

    Optional<BranchDTO> getBranchByCode(String code);

    Long getTotalBranches();

    List<BranchDTO> getActiveBranches();
}
