package com.recruitment.controller;

import com.recruitment.dto.JobSearchDTO;
import com.recruitment.model.Job;
import com.recruitment.repository.CategoryRepository;
import com.recruitment.service.JobService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class JobSearchController {

    private final JobService jobService;
    private final CategoryRepository categoryRepository;
    private final com.recruitment.service.UserService userService;
    private final com.recruitment.service.JobSeekerService jobSeekerService;
    private final com.recruitment.service.SavedJobService savedJobService;

    @GetMapping("/jobs/search")
    public String search(@ModelAttribute JobSearchDTO search,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @org.springframework.security.core.annotation.AuthenticationPrincipal org.springframework.security.core.userdetails.UserDetails userDetails,
            Model model) {

        Page<Job> jobs = jobService.searchJobs(search,
                PageRequest.of(page, size, Sort.by("createdAt").descending()));

        if (userDetails != null) {
            var user = userService.findByEmail(userDetails.getUsername());
            if (user != null && user.getRole() == com.recruitment.model.User.Role.JOB_SEEKER) {
                var profile = jobSeekerService.getProfile(user.getId());
                if (profile != null) {
                    java.util.Set<Long> savedJobIds = savedJobService.getSavedJobs(profile.getId())
                            .stream().map(com.recruitment.model.BaseEntity::getId)
                            .collect(java.util.stream.Collectors.toSet());
                    model.addAttribute("savedJobIds", savedJobIds);
                }
            }
        }

        model.addAttribute("jobs", jobs);
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());

        return "jobs/search";
    }
}
