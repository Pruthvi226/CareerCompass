package com.careercompass.service.impl;

import com.careercompass.dto.PredictionResultDTO;
import com.careercompass.dto.PredictionRequestDTO;
import com.careercompass.entity.*;
import com.careercompass.repository.CutoffRepository;
import com.careercompass.repository.ExamRepository;
import com.careercompass.repository.PredictionHistoryRepository;
import com.careercompass.repository.UserRepository;
import com.careercompass.service.PredictionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of PredictionService - Core Prediction Engine
 * 
 * Prediction Logic:
 * - Safe: student rank is much better than closing rank (difference > 5000)
 * - Moderate: student rank is near closing rank (difference 0-5000)
 * - Dream: student rank is slightly worse than closing rank (difference < 0 but not too much)
 */
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PredictionServiceImpl implements PredictionService {

    private final CutoffRepository cutoffRepository;
    private final PredictionHistoryRepository predictionHistoryRepository;
    private final UserRepository userRepository;
    private final ExamRepository examRepository;

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> predictColleges(PredictionRequestDTO request) {
        log.info("Processing prediction request for rank: {}", request.getRank());

        // Get relevant cutoffs
        Category category = Category.valueOf(request.getCategory());
        Gender gender = Gender.valueOf(request.getGender());
        Quota quota = Quota.valueOf(request.getQuota());

        List<Gender> acceptedGenders = gender == Gender.GENDER_NEUTRAL
                ? List.of(Gender.GENDER_NEUTRAL)
                : List.of(gender, Gender.GENDER_NEUTRAL);
        List<Cutoff> cutoffs = cutoffRepository.findRelevantCutoffs(
                request.getExamId(), category, acceptedGenders, quota);

        // Convert to prediction results and filter
        List<PredictionResultDTO> results = cutoffs.stream()
                .filter(cutoff -> matchesPreferredBranch(cutoff, request.getPreferredBranch()))
                .filter(cutoff -> matchesCollegeType(cutoff, request.getPreferredCollegeType()))
                .filter(cutoff -> matchesState(cutoff, request.getStatePreference()))
                .filter(cutoff -> matchesFees(cutoff, request.getMaxFees()))
                .filter(cutoff -> request.getMinClosingRank() == null || cutoff.getClosingRank() >= request.getMinClosingRank())
                .filter(cutoff -> request.getMaxClosingRank() == null || cutoff.getClosingRank() <= request.getMaxClosingRank())
                .map(cutoff -> createPredictionResult(cutoff, request))
                .filter(result -> result.getChancePercentage() > 0)
                .collect(Collectors.toMap(
                        result -> result.getCollegeId() + ":" + result.getBranchId(),
                        result -> result,
                        this::chooseMoreRelevantResult,
                        LinkedHashMap::new))
                .values()
                .stream()
                .sorted((a, b) -> Double.compare(b.getChancePercentage(), a.getChancePercentage()))
                .limit(20)
                .collect(Collectors.toList());

        log.info("Generated {} predictions for student rank {}", results.size(), request.getRank());
        return results;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> getSafeColleges(List<PredictionResultDTO> results) {
        return results.stream()
                .filter(r -> "SAFE".equals(r.getPredictionType()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> getModerateColleges(List<PredictionResultDTO> results) {
        return results.stream()
                .filter(r -> "MODERATE".equals(r.getPredictionType()))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> getDreamColleges(List<PredictionResultDTO> results) {
        return results.stream()
                .filter(r -> "DREAM".equals(r.getPredictionType()))
                .collect(Collectors.toList());
    }

    @Override
    public Double calculateChancePercentage(Integer studentRank, Integer closingRank, Integer openingRank) {
        if (closingRank == null || openingRank == null || studentRank == null) {
            return 0.0;
        }

        int rankDifference = closingRank - studentRank;
        int safeMargin = Math.max(1500, (int) Math.round(closingRank * 0.07));
        int moderateTolerance = Math.max(1500, (int) Math.round(closingRank * 0.10));
        int dreamTolerance = Math.max(3000, (int) Math.round(closingRank * 0.25));

        if (rankDifference >= safeMargin) {
            return Math.min(95.0, 80.0 + (rankDifference / (double) safeMargin) * 7.5);
        }
        if (rankDifference >= -moderateTolerance) {
            double closeness = (rankDifference + moderateTolerance) / (double) (safeMargin + moderateTolerance);
            return 50.0 + Math.max(0.0, Math.min(1.0, closeness)) * 25.0;
        }
        if (rankDifference >= -dreamTolerance) {
            double closeness = (rankDifference + dreamTolerance) / (double) (dreamTolerance - moderateTolerance);
            return 20.0 + Math.max(0.0, Math.min(1.0, closeness)) * 25.0;
        }
        return 0.0;
    }

    @Override
    public String getPredictionType(Integer studentRank, Integer closingRank) {
        if (studentRank == null || closingRank == null) {
            return "UNKNOWN";
        }

        int rankDifference = closingRank - studentRank;
        int safeMargin = Math.max(1500, (int) Math.round(closingRank * 0.07));
        int moderateTolerance = Math.max(1500, (int) Math.round(closingRank * 0.10));
        int dreamTolerance = Math.max(3000, (int) Math.round(closingRank * 0.25));

        if (rankDifference >= safeMargin) {
            return "SAFE";
        }
        else if (rankDifference >= -moderateTolerance) {
            return "MODERATE";
        }
        else if (rankDifference >= -dreamTolerance) {
            return "DREAM";
        }
        return "OUT_OF_RANGE";
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> getRecommendedColleges(PredictionRequestDTO request) {
        List<PredictionResultDTO> allPredictions = predictColleges(request);

        // Get safe colleges first, then moderate, then dream
        List<PredictionResultDTO> safe = getSafeColleges(allPredictions);
        List<PredictionResultDTO> moderate = getModerateColleges(allPredictions);
        List<PredictionResultDTO> dream = getDreamColleges(allPredictions);

        // Combine and sort by NIRF rank (if available)
        List<PredictionResultDTO> recommended = safe;
        if (recommended.size() < 10) {
            recommended.addAll(moderate.stream()
                    .limit(10 - recommended.size())
                    .collect(Collectors.toList()));
        }
        if (recommended.size() < 15) {
            recommended.addAll(dream.stream()
                    .limit(15 - recommended.size())
                    .collect(Collectors.toList()));
        }

        return recommended;
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> getChoiceFillingOrder(List<PredictionResultDTO> predictions) {
        // Order by: Dream -> Moderate -> Safe (for choice filling)
        List<PredictionResultDTO> ordered = predictions.stream()
                .sorted((a, b) -> {
                    int typeOrder = getTypeOrder(a.getPredictionType()) - getTypeOrder(b.getPredictionType());
                    if (typeOrder != 0) return typeOrder;
                    // Within same type, sort by NIRF rank
                    return a.getNirfRank() != null && b.getNirfRank() != null
                            ? a.getNirfRank().compareTo(b.getNirfRank())
                            : 0;
                })
                .collect(Collectors.toList());

        return ordered;
    }

    @Override
    public void recordPrediction(Long userId, PredictionRequestDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new com.careercompass.exception.ResourceNotFoundException("User not found"));
        Exam exam = examRepository.findById(request.getExamId())
                .orElseThrow(() -> new com.careercompass.exception.ResourceNotFoundException("Exam not found"));

        PredictionHistory history = PredictionHistory.builder()
                .user(user)
                .exam(exam)
                .rank(request.getRank())
                .category(Category.valueOf(request.getCategory()))
                .gender(Gender.valueOf(request.getGender()))
                .quota(Quota.valueOf(request.getQuota()))
                .homeState(request.getHomeState())
                .preferredBranch(request.getPreferredBranch())
                .preferredCollegeType(request.getPreferredCollegeType())
                .build();
        predictionHistoryRepository.save(history);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getTotalPredictions() {
        return predictionHistoryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> filterByBranch(List<PredictionResultDTO> results, String branchName) {
        return results.stream()
                .filter(r -> r.getBranchName() != null && r.getBranchName().equalsIgnoreCase(branchName))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PredictionResultDTO> filterByFeeRange(List<PredictionResultDTO> results, Double minFees, Double maxFees) {
        return results.stream()
                .filter(r -> {
                    if (r.getFees() == null) return true;
                    double fees = r.getFees().doubleValue();
                    return (minFees == null || fees >= minFees) && (maxFees == null || fees <= maxFees);
                })
                .collect(Collectors.toList());
    }

    /**
     * Create prediction result for a single cutoff record
     */
    private PredictionResultDTO createPredictionResult(Cutoff cutoff, PredictionRequestDTO request) {
        Double chancePercentage = calculateChancePercentage(
                request.getRank(), cutoff.getClosingRank(), cutoff.getOpeningRank());

        String predictionType = getPredictionType(request.getRank(), cutoff.getClosingRank());

        int rankDifference = cutoff.getClosingRank() - request.getRank();

        String advice = getAdvice(predictionType, rankDifference);

        return PredictionResultDTO.builder()
                .collegeId(cutoff.getCollege().getId())
                .collegeName(cutoff.getCollege().getName())
                .collegeType(cutoff.getCollege().getCollegeType().getDisplayName())
                .counsellingType(cutoff.getCollege().getCounsellingType())
                .city(cutoff.getCollege().getCity())
                .state(cutoff.getCollege().getState())
                .branchId(cutoff.getBranch().getId())
                .branchName(cutoff.getBranch().getName())
                .branchCode(cutoff.getBranch().getBranchCode())
                .previousClosingRank(cutoff.getClosingRank())
                .studentRank(request.getRank())
                .chancePercentage(Math.round(chancePercentage * 100.0) / 100.0)
                .predictionType(predictionType)
                .fees(cutoff.getCollege().getFees())
                .averagePackage(cutoff.getCollege().getAveragePackage())
                .highestPackage(cutoff.getCollege().getHighestPackage())
                .placementPercentage(cutoff.getCollege().getPlacementPercentage())
                .nirfRank(cutoff.getCollege().getNirfRank())
                .seatIntake(cutoff.getCollege().getSeatIntake())
                .bondDetails(cutoff.getCollege().getBondDetails())
                .stipendDetails(cutoff.getCollege().getStipendDetails())
                .hospitalAttached(cutoff.getCollege().getHospitalAttached())
                .advice(advice)
                .rankDifference(rankDifference)
                .year(cutoff.getYear())
                .build();
    }

    private boolean matchesPreferredBranch(Cutoff cutoff, String preferredBranch) {
        if (preferredBranch == null || preferredBranch.isBlank()) {
            return true;
        }
        String preference = preferredBranch.trim().toLowerCase();
        return cutoff.getBranch().getName().toLowerCase().contains(preference)
                || cutoff.getBranch().getBranchCode().toLowerCase().contains(preference);
    }

    private boolean matchesCollegeType(Cutoff cutoff, String preferredCollegeType) {
        if (preferredCollegeType == null || preferredCollegeType.isBlank()) {
            return true;
        }
        return cutoff.getCollege().getCollegeType().name().equalsIgnoreCase(preferredCollegeType.trim());
    }

    private boolean matchesState(Cutoff cutoff, String statePreference) {
        if (statePreference == null || statePreference.isBlank()) {
            return true;
        }
        return cutoff.getCollege().getState().equalsIgnoreCase(statePreference.trim());
    }

    private boolean matchesFees(Cutoff cutoff, BigDecimal maxFees) {
        if (maxFees == null || cutoff.getCollege().getFees() == null) {
            return true;
        }
        return cutoff.getCollege().getFees().compareTo(maxFees) <= 0;
    }

    private PredictionResultDTO chooseMoreRelevantResult(PredictionResultDTO current, PredictionResultDTO candidate) {
        if (candidate.getYear() != null && current.getYear() != null) {
            int yearCompare = candidate.getYear().compareTo(current.getYear());
            if (yearCompare != 0) {
                return yearCompare > 0 ? candidate : current;
            }
        }
        return candidate.getChancePercentage() >= current.getChancePercentage() ? candidate : current;
    }

    /**
     * Generate advice based on prediction type
     */
    private String getAdvice(String predictionType, int rankDifference) {
        switch (predictionType) {
            case "SAFE":
                return "Excellent chance! This is a safe choice. Consider filling this in your form.";
            case "MODERATE":
                return "Good chance! You have a fair probability of admission. Worth considering in your choices.";
            case "DREAM":
                return "Challenging choice! Admission chances are low but possible. Can be your dream choice.";
            default:
                return "Counselling prediction based on historical cutoff data.";
        }
    }

    /**
     * Get type order for sorting (0=DREAM, 1=MODERATE, 2=SAFE)
     */
    private int getTypeOrder(String type) {
        switch (type) {
            case "DREAM":
                return 0;
            case "MODERATE":
                return 1;
            case "SAFE":
                return 2;
            default:
                return 3;
        }
    }
}
