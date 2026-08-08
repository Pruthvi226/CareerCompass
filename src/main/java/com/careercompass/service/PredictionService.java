package com.careercompass.service;

import com.careercompass.dto.PredictionResultDTO;
import com.careercompass.dto.PredictionRequestDTO;
import java.util.List;

/**
 * Prediction service interface - core prediction engine
 */
public interface PredictionService {

    List<PredictionResultDTO> predictColleges(PredictionRequestDTO request);

    List<PredictionResultDTO> getSafeColleges(List<PredictionResultDTO> results);

    List<PredictionResultDTO> getModerateColleges(List<PredictionResultDTO> results);

    List<PredictionResultDTO> getDreamColleges(List<PredictionResultDTO> results);

    Double calculateChancePercentage(Integer studentRank, Integer closingRank, Integer openingRank);

    String getPredictionType(Integer studentRank, Integer closingRank);

    List<PredictionResultDTO> getRecommendedColleges(PredictionRequestDTO request);

    List<PredictionResultDTO> getChoiceFillingOrder(List<PredictionResultDTO> predictions);

    void recordPrediction(Long userId, PredictionRequestDTO request);

    Long getTotalPredictions();

    List<PredictionResultDTO> filterByBranch(List<PredictionResultDTO> results, String branchName);

    List<PredictionResultDTO> filterByFeeRange(List<PredictionResultDTO> results, 
                                               Double minFees, Double maxFees);
}
