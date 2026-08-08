package com.careercompass.service.impl;

import com.careercompass.dto.PredictionRequestDTO;
import com.careercompass.dto.PredictionResultDTO;
import com.careercompass.entity.Branch;
import com.careercompass.entity.Category;
import com.careercompass.entity.College;
import com.careercompass.entity.CollegeType;
import com.careercompass.entity.Cutoff;
import com.careercompass.entity.DegreeType;
import com.careercompass.entity.Exam;
import com.careercompass.entity.ExamType;
import com.careercompass.entity.Gender;
import com.careercompass.entity.Quota;
import com.careercompass.repository.CutoffRepository;
import com.careercompass.repository.ExamRepository;
import com.careercompass.repository.PredictionHistoryRepository;
import com.careercompass.repository.UserRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PredictionServiceImplTest {

    @Mock
    private CutoffRepository cutoffRepository;

    @Mock
    private PredictionHistoryRepository predictionHistoryRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ExamRepository examRepository;

    @InjectMocks
    private PredictionServiceImpl predictionService;

    @Test
    void classifiesSafeModerateAndDreamRankBands() {
        assertThat(predictionService.getPredictionType(1_000, 10_000)).isEqualTo("SAFE");
        assertThat(predictionService.getPredictionType(9_500, 10_000)).isEqualTo("MODERATE");
        assertThat(predictionService.getPredictionType(12_000, 10_000)).isEqualTo("DREAM");
        assertThat(predictionService.getPredictionType(20_000, 10_000)).isEqualTo("OUT_OF_RANGE");
    }

    @Test
    void predictionResultsPreferLatestCutoffForSameCollegeAndBranch() {
        PredictionRequestDTO request = PredictionRequestDTO.builder()
                .examId(1L)
                .rank(2_500)
                .category("GENERAL")
                .gender("GENDER_NEUTRAL")
                .quota("ALL_INDIA")
                .build();

        when(cutoffRepository.findRelevantCutoffs(
                1L,
                Category.GENERAL,
                List.of(Gender.GENDER_NEUTRAL),
                Quota.ALL_INDIA
        )).thenReturn(List.of(
                cutoff(1L, 1_000, 3_200, 2024),
                cutoff(2L, 1_200, 2_900, 2025)
        ));

        List<PredictionResultDTO> results = predictionService.predictColleges(request);

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getYear()).isEqualTo(2025);
        assertThat(results.get(0).getPreviousClosingRank()).isEqualTo(2_900);
    }

    private Cutoff cutoff(Long id, Integer openingRank, Integer closingRank, Integer year) {
        Exam exam = Exam.builder()
                .id(1L)
                .examType(ExamType.JEE_MAIN)
                .description("JEE Main")
                .build();
        College college = College.builder()
                .id(1L)
                .name("NIT Warangal")
                .collegeType(CollegeType.NIT)
                .city("Warangal")
                .state("Telangana")
                .build();
        Branch branch = Branch.builder()
                .id(1L)
                .name("Computer Science and Engineering")
                .branchCode("CSE")
                .degreeType(DegreeType.BTECH)
                .duration(4)
                .build();

        return Cutoff.builder()
                .id(id)
                .exam(exam)
                .college(college)
                .branch(branch)
                .category(Category.GENERAL)
                .gender(Gender.GENDER_NEUTRAL)
                .quota(Quota.ALL_INDIA)
                .roundNumber(1)
                .openingRank(openingRank)
                .closingRank(closingRank)
                .year(year)
                .build();
    }
}
