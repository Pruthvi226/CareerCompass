package com.careercompass.service.impl;

import com.careercompass.dto.BranchDTO;
import com.careercompass.entity.Branch;
import com.careercompass.exception.DuplicateResourceException;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.BranchRepository;
import com.careercompass.service.BranchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of BranchService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class BranchServiceImpl implements BranchService {

    private final BranchRepository branchRepository;

    @Override
    public BranchDTO addBranch(BranchDTO branchDTO) {
        if (branchRepository.existsByBranchCode(branchDTO.getBranchCode())) {
            throw new DuplicateResourceException(
                    "Branch with code " + branchDTO.getBranchCode() + " already exists");
        }

        Branch branch = Branch.builder()
                .name(branchDTO.getName())
                .branchCode(branchDTO.getBranchCode())
                .degreeType(com.careercompass.entity.DegreeType.valueOf(branchDTO.getDegreeType()))
                .duration(branchDTO.getDuration())
                .description(branchDTO.getDescription())
                .isActive(true)
                .build();

        Branch savedBranch = branchRepository.save(branch);
        return mapToDTO(savedBranch);
    }

    @Override
    public Optional<BranchDTO> getBranchById(Long id) {
        return branchRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getAllBranches() {
        return branchRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BranchDTO updateBranch(Long id, BranchDTO branchDTO) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id " + id));

        branch.setName(branchDTO.getName());
        branch.setDuration(branchDTO.getDuration());
        branch.setDescription(branchDTO.getDescription());
        if (branchDTO.getDegreeType() != null) {
            branch.setDegreeType(com.careercompass.entity.DegreeType.valueOf(branchDTO.getDegreeType()));
        }

        Branch updatedBranch = branchRepository.save(branch);
        return mapToDTO(updatedBranch);
    }

    @Override
    public void deleteBranch(Long id) {
        Branch branch = branchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Branch not found with id " + id));
        branchRepository.delete(branch);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> searchBranches(String keyword) {
        return branchRepository.searchActiveBranches(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BranchDTO> getBranchByCode(String code) {
        return branchRepository.findByBranchCode(code).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalBranches() {
        return branchRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<BranchDTO> getActiveBranches() {
        return branchRepository.findByIsActive(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private BranchDTO mapToDTO(Branch branch) {
        return BranchDTO.builder()
                .id(branch.getId())
                .name(branch.getName())
                .branchCode(branch.getBranchCode())
                .degreeType(branch.getDegreeType().toString())
                .duration(branch.getDuration())
                .description(branch.getDescription())
                .isActive(branch.getIsActive())
                .build();
    }
}
