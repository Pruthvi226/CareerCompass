package com.careercompass.controller;

import com.careercompass.dto.BranchDTO;
import com.careercompass.dto.CollegeDTO;
import com.careercompass.dto.ExamDTO;
import com.careercompass.entity.Exam;
import com.careercompass.entity.ExamType;
import com.careercompass.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.io.InputStream;

/**
 * Admin controller for college and cutoff management
 */
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final CollegeService collegeService;
    private final BranchService branchService;
    private final CSVImportService csvImportService;
    private final AnalyticsService analyticsService;
    private final ExamService examService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String adminDashboard(Model model) {
        var stats = analyticsService.getAdminDashboardStats();
        model.addAttribute("stats", stats);
        return "admin/dashboard";
    }

    @GetMapping("/colleges")
    public String manageColleges(Model model) {
        model.addAttribute("colleges", collegeService.getAllColleges());
        return "admin/manage-colleges";
    }

    @GetMapping("/colleges/add")
    public String showAddCollegeForm(Model model) {
        model.addAttribute("collegeDTO", new CollegeDTO());
        model.addAttribute("collegeTypes", com.careercompass.entity.CollegeType.values());
        return "admin/add-college";
    }

    @PostMapping("/colleges/add")
    public String addCollege(@ModelAttribute CollegeDTO collegeDTO,
                              RedirectAttributes redirectAttributes) {
        try {
            collegeService.addCollege(collegeDTO);
            redirectAttributes.addFlashAttribute("successMessage", "College added successfully");
            return "redirect:/admin/colleges";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/colleges/add";
        }
    }

    @GetMapping("/branches")
    public String manageBranches(Model model) {
        model.addAttribute("branches", branchService.getAllBranches());
        return "admin/manage-branches";
    }

    @GetMapping("/branches/add")
    public String showAddBranchForm(Model model) {
        model.addAttribute("branchDTO", new BranchDTO());
        model.addAttribute("degreeTypes", com.careercompass.entity.DegreeType.values());
        return "admin/add-branch";
    }

    @PostMapping("/branches/add")
    public String addBranch(@ModelAttribute BranchDTO branchDTO,
                             RedirectAttributes redirectAttributes) {
        try {
            branchService.addBranch(branchDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Branch added successfully");
            return "redirect:/admin/branches";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return "redirect:/admin/branches/add";
        }
    }

    @GetMapping("/cutoffs/upload")
    public String showUploadCutoffForm() {
        return "admin/upload-cutoff";
    }

    @PostMapping("/cutoffs/upload")
    public String uploadCutoffCSV(@RequestParam("file") MultipartFile file,
                                   RedirectAttributes redirectAttributes) {
        try {
            if (file.isEmpty()) {
                redirectAttributes.addFlashAttribute("errorMessage", "Please select a file");
                return "redirect:/admin/cutoffs/upload";
            }

            InputStream inputStream = file.getInputStream();
            int recordsImported = csvImportService.importCutoffData(inputStream);
            
            redirectAttributes.addFlashAttribute("successMessage",
                    "CSV uploaded successfully! " + recordsImported + " records imported.");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage",
                    "Error uploading CSV: " + e.getMessage());
            return "redirect:/admin/cutoffs/upload";
        }
    }

    @GetMapping("/analytics")
    public String viewAnalytics(Model model) {
        var stats = analyticsService.getAdminDashboardStats();
        model.addAttribute("stats", stats);
        model.addAttribute("predictionsByExam", analyticsService.getPredictionsByExam());
        model.addAttribute("collegesByType", analyticsService.getCollegesByType());
        model.addAttribute("studentsByCategory", analyticsService.getStudentsByCategory());
        return "admin/analytics";
    }

    @GetMapping("/exams")
    public String manageExams(Model model) {
        model.addAttribute("exams", examService.getAllExams());
        model.addAttribute("examTypes", ExamType.values());
        model.addAttribute("examDTO", new ExamDTO());
        return "admin/manage-exams";
    }

    @PostMapping("/exams/add")
    public String addExam(@ModelAttribute ExamDTO examDTO, RedirectAttributes redirectAttributes) {
        try {
            Exam exam = Exam.builder()
                    .examType(ExamType.valueOf(examDTO.getExamType()))
                    .description(examDTO.getDescription())
                    .counsellingBody(examDTO.getCounsellingBody())
                    .isActive(true)
                    .build();
            examService.addExam(exam);
            redirectAttributes.addFlashAttribute("successMessage", "Exam added successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/exams";
    }

    @GetMapping("/users")
    public String manageUsers(Model model) {
        model.addAttribute("students", userService.getAllStudents());
        return "admin/manage-users";
    }

    @PostMapping("/users/delete/{userId}")
    public String deleteUser(@PathVariable Long userId, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(userId);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/admin/users";
    }
}
