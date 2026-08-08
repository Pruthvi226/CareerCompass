package com.careercompass.repository;

import com.careercompass.entity.Branch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository for Branch entity
 */
@Repository
public interface BranchRepository extends JpaRepository<Branch, Long> {

    Optional<Branch> findByBranchCode(String branchCode);

    List<Branch> findByIsActive(Boolean isActive);

    List<Branch> findByNameContainingIgnoreCase(String name);

    @Query("SELECT b FROM Branch b WHERE LOWER(b.name) LIKE LOWER(CONCAT('%', :name, '%')) AND b.isActive = true ORDER BY b.name")
    List<Branch> searchActiveBranches(String name);

    boolean existsByBranchCode(String branchCode);
}
