package com.careercompass.controller;

import com.careercompass.dto.CollegeDTO;
import com.careercompass.service.CollegeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import java.util.List;

/**
 * Controller for college listing and details
 */
@Controller
@RequestMapping("/colleges")
@RequiredArgsConstructor
public class CollegeController {

    private final CollegeService collegeService;

    @GetMapping
    public String listColleges(@RequestParam(required = false) String state,
                                @RequestParam(required = false) String type,
                                @RequestParam(required = false) String search,
                                Model model) {
        List<CollegeDTO> colleges;

        if (search != null && !search.isBlank()) {
            colleges = collegeService.searchColleges(search);
        } else if (state != null && !state.isBlank()) {
            colleges = collegeService.getCollegesByState(state);
        } else if (type != null && !type.isBlank()) {
            colleges = collegeService.getCollegesByType(type);
        } else {
            colleges = collegeService.getActiveColleges();
        }

        model.addAttribute("colleges", colleges);
        model.addAttribute("states", collegeService.getAllStates());
        model.addAttribute("totalColleges", collegeService.getTotalColleges());
        return "colleges";
    }

    @GetMapping("/{id}")
    public String getCollegeDetails(@PathVariable Long id, Model model) {
        var college = collegeService.getCollegeById(id);
        if (college.isPresent()) {
            model.addAttribute("college", college.get());
            return "college-details";
        }
        return "redirect:/colleges";
    }
}
