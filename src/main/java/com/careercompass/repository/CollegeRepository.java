package com.careercompass.repository;

import com.careercompass.entity.College;
import com.careercompass.entity.CollegeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repository for College entity
 */
@Repository
public interface CollegeRepository extends JpaRepository<College, Long> {

    List<College> findByState(String state);

    List<College> findByCollegeType(CollegeType collegeType);

    List<College> findByIsActive(Boolean isActive);

    List<College> findByStateAndCollegeType(String state, CollegeType collegeType);

    List<College> findByCity(String city);

    List<College> findByStateAndCity(String state, String city);

    List<College> findByNameContainingIgnoreCase(String name);

    @Query("SELECT c FROM College c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%')) AND c.isActive = true ORDER BY c.nirfRank ASC NULLS LAST")
    List<College> searchActiveColleges(@Param("name") String name);

    @Query("SELECT DISTINCT c.state FROM College c WHERE c.isActive = true ORDER BY c.state")
    List<String> findDistinctStates();
}
