package com.careercompass.controller;

import com.careercompass.dto.WishlistDTO;
import com.careercompass.dto.UserDTO;
import com.careercompass.dto.CollegeDTO;
import com.careercompass.dto.CutoffDTO;
import com.careercompass.entity.Category;
import com.careercompass.service.AnalyticsService;
import com.careercompass.service.CollegeService;
import com.careercompass.service.CutoffService;
import com.careercompass.service.ExamService;
import com.careercompass.service.WishlistService;
import com.careercompass.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Controller for student dashboard and profile
 */
@Controller
@RequestMapping("/student")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final WishlistService wishlistService;
    private final UserService userService;
    private final AnalyticsService analyticsService;
    private final CollegeService collegeService;
    private final ExamService examService;
    private final CutoffService cutoffService;

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            Long wishlistCount = wishlistService.getWishlistCount(user.get().getId());
            model.addAttribute("user", user.get());
            model.addAttribute("categories", Category.values());
            model.addAttribute("selectedCategory", user.get().getCategory() != null ? user.get().getCategory().name() : "");
            model.addAttribute("wishlistCount", wishlistCount);
            model.addAttribute("stats", analyticsService.getStudentDashboardStats(user.get().getId()));
            return "student/dashboard";
        }
        return "redirect:/login";
    }

    @GetMapping("/profile")
    public String viewProfile(Model model, Authentication auth) {
        var user = userService.findByEmail(auth.getName());
        model.addAttribute("user", user.orElse(null));
        model.addAttribute("userDTO", user.map(value -> UserDTO.builder()
                .id(value.getId())
                .fullName(value.getFullName())
                .email(value.getEmail())
                .phone(value.getPhone())
                .state(value.getState())
                .category(value.getCategory() != null ? value.getCategory().name() : null)
                .gender(value.getGender() != null ? value.getGender().name() : null)
                .build()).orElse(new UserDTO()));
        return "student/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@ModelAttribute UserDTO userDTO, Authentication auth,
                                RedirectAttributes redirectAttributes) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            userService.updateProfile(user.get().getId(), userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Profile updated successfully");
        }
        return "redirect:/student/profile";
    }

    @PostMapping("/dashboard/category")
    public String updateDashboardCategory(@RequestParam String category, Authentication auth,
                                          RedirectAttributes redirectAttributes) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            try {
                userService.updateCategory(user.get().getId(), category);
                redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully");
            } catch (IllegalArgumentException e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a valid category");
            }
        }
        return "redirect:/student/dashboard";
    }

    @GetMapping("/wishlist")
    public String viewWishlist(Model model, Authentication auth) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            List<WishlistDTO> wishlist = wishlistService.getUserWishlist(user.get().getId());
            model.addAttribute("wishlist", wishlist);
            model.addAttribute("total", wishlist.size());
            return "student/wishlist";
        }
        return "redirect:/login";
    }

    @PostMapping("/wishlist/add/{collegeId}")
    public String addToWishlist(@PathVariable Long collegeId, Authentication auth,
                                 RedirectAttributes redirectAttributes) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            try {
                wishlistService.addToWishlist(user.get().getId(), collegeId, null, null);
                redirectAttributes.addFlashAttribute("successMessage", "Added to wishlist!");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to add to wishlist");
            }
        }
        return "redirect:/colleges/" + collegeId;
    }

    @PostMapping("/wishlist/remove/{wishlistId}")
    public String removeFromWishlist(@PathVariable Long wishlistId, Authentication auth,
                                     RedirectAttributes redirectAttributes) {
        var user = userService.findByEmail(auth.getName());
        if (user.isPresent()) {
            try {
                wishlistService.removeWishlistItem(user.get().getId(), wishlistId);
                redirectAttributes.addFlashAttribute("successMessage", "Removed from wishlist");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("errorMessage", "Failed to remove from wishlist");
            }
        }
        return "redirect:/student/wishlist";
    }

    @GetMapping("/compare")
    public String compareColleges(@RequestParam(required = false) List<Long> collegeIds, Model model) {
        List<CollegeDTO> selected = new ArrayList<>();
        if (collegeIds != null) {
            selected = collegeIds.stream()
                    .limit(3)
                    .map(collegeService::getCollegeById)
                    .flatMap(java.util.Optional::stream)
                    .collect(Collectors.toList());
        }

        model.addAttribute("colleges", collegeService.getActiveColleges());
        model.addAttribute("selectedColleges", selected);
        return "student/compare";
    }

    @GetMapping("/trends")
    public String cutoffTrends(@RequestParam(required = false) Long examId, Model model) {
        List<CutoffDTO> cutoffs = examId != null ? cutoffService.getCutoffsByExam(examId) : List.of();
        var orderedCutoffs = cutoffs.stream()
                .sorted(Comparator.comparing(c -> c.getYear() + "-" + c.getRoundNumber()))
                .collect(Collectors.toList());

        model.addAttribute("exams", examService.getActiveExams());
        model.addAttribute("selectedExamId", examId);
        model.addAttribute("trendLabels", orderedCutoffs.stream()
                .map(c -> c.getYear() + " R" + c.getRoundNumber())
                .collect(Collectors.toList()));
        model.addAttribute("trendValues", orderedCutoffs.stream()
                .map(c -> c.getClosingRank())
                .collect(Collectors.toList()));
        model.addAttribute("cutoffs", orderedCutoffs);
        return "student/trends";
    }
}
