package com.careercompass.service;

import com.careercompass.dto.CollegeDTO;
import java.util.List;
import java.util.Optional;

/**
 * College service interface
 */
public interface CollegeService {

    CollegeDTO addCollege(CollegeDTO collegeDTO);

    Optional<CollegeDTO> getCollegeById(Long id);

    List<CollegeDTO> getAllColleges();

    List<CollegeDTO> getCollegesByState(String state);

    List<CollegeDTO> getCollegesByType(String collegeType);

    List<CollegeDTO> getCollegesByCity(String city);

    CollegeDTO updateCollege(Long id, CollegeDTO collegeDTO);

    void deleteCollege(Long id);

    List<CollegeDTO> searchColleges(String keyword);

    List<String> getAllStates();

    Long getTotalColleges();

    List<CollegeDTO> getActiveColleges();
}
