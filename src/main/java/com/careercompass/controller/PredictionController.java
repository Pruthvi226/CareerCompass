package com.careercompass.controller;

import com.careercompass.dto.PredictionRequestDTO;
import com.careercompass.dto.PredictionResultDTO;
import com.careercompass.entity.ExamType;
import com.careercompass.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * Controller for college prediction functionality
 */
@Controller
@RequestMapping("/student/predictor")
@RequiredArgsConstructor
public class PredictionController {

    private final PredictionService predictionService;
    private final ExamService examService;
    private final BranchService branchService;
    private final WishlistService wishlistService;
    private final UserService userService;

    @GetMapping
    public String showPredictorForm(Model model) {
        model.addAttribute("exams", examService.getActiveExams());
        model.addAttribute("branches", branchService.getActiveBranches());
        model.addAttribute("predictionRequest", new PredictionRequestDTO());
        return "student/predictor-form";
    }

    @PostMapping("/predict")
    public String predictColleges(@ModelAttribute PredictionRequestDTO request,
                                   Model model, Authentication auth) {
        List<PredictionResultDTO> predictions = predictionService.predictColleges(request);

        // Check wishlist for current user
        var currentUser = userService.findByEmail(auth.getName());
        if (currentUser.isPresent()) {
            predictionService.recordPrediction(currentUser.get().getId(), request);
            predictions.forEach(p -> {
                boolean inWishlist = wishlistService.isInWishlist(currentUser.get().getId(), p.getCollegeId());
                p.setInWishlist(inWishlist);
            });
        }

        model.addAttribute("predictions", predictions);
        model.addAttribute("safe", predictionService.getSafeColleges(predictions));
        model.addAttribute("moderate", predictionService.getModerateColleges(predictions));
        model.addAttribute("dream", predictionService.getDreamColleges(predictions));
        model.addAttribute("choiceOrder", predictionService.getChoiceFillingOrder(predictions));
        model.addAttribute("totalPredictions", predictions.size());

        return "student/prediction-results";
    }

    @GetMapping("/choice-filling")
    public String choiceFillingPlanner(Model model) {
        model.addAttribute("exams", examService.getActiveExams());
        model.addAttribute("branches", branchService.getActiveBranches());
        model.addAttribute("predictionRequest", new PredictionRequestDTO());
        return "student/choice-filling";
    }

    @PostMapping("/choice-filling")
    public String generateChoiceFilling(@ModelAttribute PredictionRequestDTO request, Model model) {
        List<PredictionResultDTO> predictions = predictionService.predictColleges(request);
        model.addAttribute("exams", examService.getActiveExams());
        model.addAttribute("branches", branchService.getActiveBranches());
        model.addAttribute("predictionRequest", request);
        model.addAttribute("choiceOrder", predictionService.getChoiceFillingOrder(predictions));
        return "student/choice-filling";
    }
}
