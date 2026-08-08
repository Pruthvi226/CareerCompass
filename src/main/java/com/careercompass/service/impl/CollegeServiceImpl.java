package com.careercompass.service.impl;

import com.careercompass.dto.CollegeDTO;
import com.careercompass.entity.College;
import com.careercompass.entity.CollegeType;
import com.careercompass.exception.ResourceNotFoundException;
import com.careercompass.repository.CollegeRepository;
import com.careercompass.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of CollegeService
 */
@Service
@RequiredArgsConstructor
@Transactional
public class CollegeServiceImpl implements CollegeService {

    private final CollegeRepository collegeRepository;

    @Override
    public CollegeDTO addCollege(CollegeDTO collegeDTO) {
        College college = College.builder()
                .name(collegeDTO.getName())
                .collegeType(CollegeType.valueOf(collegeDTO.getCollegeType()))
                .city(collegeDTO.getCity())
                .state(collegeDTO.getState())
                .fees(collegeDTO.getFees())
                .averagePackage(collegeDTO.getAveragePackage())
                .highestPackage(collegeDTO.getHighestPackage())
                .placementPercentage(collegeDTO.getPlacementPercentage())
                .nirfRank(collegeDTO.getNirfRank())
                .website(collegeDTO.getWebsite())
                .hostelAvailable(collegeDTO.getHostelAvailable() != null ? collegeDTO.getHostelAvailable() : false)
                .campusSize(collegeDTO.getCampusSize())
                .description(collegeDTO.getDescription())
                .counsellingType(collegeDTO.getCounsellingType())
                .seatIntake(collegeDTO.getSeatIntake())
                .bondDetails(collegeDTO.getBondDetails())
                .stipendDetails(collegeDTO.getStipendDetails())
                .hospitalAttached(collegeDTO.getHospitalAttached() != null ? collegeDTO.getHospitalAttached() : false)
                .annualPatientFlow(collegeDTO.getAnnualPatientFlow())
                .isActive(true)
                .build();

        College savedCollege = collegeRepository.save(college);
        return mapToDTO(savedCollege);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<CollegeDTO> getCollegeById(Long id) {
        return collegeRepository.findById(id).map(this::mapToDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getAllColleges() {
        return collegeRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getCollegesByState(String state) {
        return collegeRepository.findByState(state).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getCollegesByType(String collegeType) {
        try {
            CollegeType type = CollegeType.valueOf(collegeType);
            return collegeRepository.findByCollegeType(type).stream()
                    .map(this::mapToDTO)
                    .collect(Collectors.toList());
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getCollegesByCity(String city) {
        return collegeRepository.findByCity(city).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CollegeDTO updateCollege(Long id, CollegeDTO collegeDTO) {
        College college = collegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with id " + id));

        college.setName(collegeDTO.getName());
        college.setCity(collegeDTO.getCity());
        college.setState(collegeDTO.getState());
        college.setFees(collegeDTO.getFees());
        college.setAveragePackage(collegeDTO.getAveragePackage());
        college.setHighestPackage(collegeDTO.getHighestPackage());
        college.setPlacementPercentage(collegeDTO.getPlacementPercentage());
        college.setNirfRank(collegeDTO.getNirfRank());
        college.setWebsite(collegeDTO.getWebsite());
        college.setHostelAvailable(collegeDTO.getHostelAvailable());
        college.setCampusSize(collegeDTO.getCampusSize());
        college.setDescription(collegeDTO.getDescription());
        college.setCounsellingType(collegeDTO.getCounsellingType());
        college.setSeatIntake(collegeDTO.getSeatIntake());
        college.setBondDetails(collegeDTO.getBondDetails());
        college.setStipendDetails(collegeDTO.getStipendDetails());
        college.setHospitalAttached(collegeDTO.getHospitalAttached());
        college.setAnnualPatientFlow(collegeDTO.getAnnualPatientFlow());

        College updatedCollege = collegeRepository.save(college);
        return mapToDTO(updatedCollege);
    }

    @Override
    public void deleteCollege(Long id) {
        College college = collegeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("College not found with id " + id));
        collegeRepository.delete(college);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> searchColleges(String keyword) {
        return collegeRepository.searchActiveColleges(keyword).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getAllStates() {
        return collegeRepository.findDistinctStates();
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalColleges() {
        return collegeRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<CollegeDTO> getActiveColleges() {
        return collegeRepository.findByIsActive(true).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private CollegeDTO mapToDTO(College college) {
        return CollegeDTO.builder()
                .id(college.getId())
                .name(college.getName())
                .collegeType(college.getCollegeType().toString())
                .city(college.getCity())
                .state(college.getState())
                .fees(college.getFees())
                .averagePackage(college.getAveragePackage())
                .highestPackage(college.getHighestPackage())
                .placementPercentage(college.getPlacementPercentage())
                .nirfRank(college.getNirfRank())
                .website(college.getWebsite())
                .hostelAvailable(college.getHostelAvailable())
                .campusSize(college.getCampusSize())
                .description(college.getDescription())
                .counsellingType(college.getCounsellingType())
                .seatIntake(college.getSeatIntake())
                .bondDetails(college.getBondDetails())
                .stipendDetails(college.getStipendDetails())
                .hospitalAttached(college.getHospitalAttached())
                .annualPatientFlow(college.getAnnualPatientFlow())
                .isActive(college.getIsActive())
                .build();
    }
}
