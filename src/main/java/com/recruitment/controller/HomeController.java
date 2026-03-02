package com.recruitment.controller;

import com.recruitment.model.Job;
import com.recruitment.repository.CategoryRepository;
import com.recruitment.service.ApplicationService;
import com.recruitment.service.JobService;
import com.recruitment.service.JobSeekerService;
import com.recruitment.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final JobService jobService;
    private final CategoryRepository categoryRepository;
    private final ApplicationService applicationService;
    private final JobSeekerService jobSeekerService;
    private final UserService userService;

    @GetMapping("/")
    public String home(Model model) {
        Page<Job> recentJobs = jobService.getApprovedJobs(PageRequest.of(0, 8, Sort.by("createdAt").descending()));
        model.addAttribute("jobs", recentJobs);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "home";
    }

    @GetMapping("/jobs")
    public String jobs(@RequestParam(defaultValue = "0") int page,
                       @RequestParam(defaultValue = "12") int size,
                       Model model) {
        Page<Job> jobs = jobService.getApprovedJobs(PageRequest.of(page, size, Sort.by("createdAt").descending()));
        model.addAttribute("jobs", jobs);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "jobs/list";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, 
                           @AuthenticationPrincipal UserDetails userDetails,
                           Model model) {
        Job job = jobService.getJob(id);
        if (job == null || job.getStatus() != Job.Status.APPROVED) {
            return "redirect:/jobs";
        }
        model.addAttribute("job", job);
        
        if (userDetails != null) {
            var user = userService.findByEmail(userDetails.getUsername());
            if (user != null) {
                boolean isJobSeeker = user.getRole() == com.recruitment.model.User.Role.JOB_SEEKER;
                model.addAttribute("isJobSeeker", isJobSeeker);
                
                var profile = jobSeekerService.getProfile(user.getId());
                if (profile != null) {
                    boolean alreadyApplied = applicationService.hasApplied(profile.getId(), id);
                    model.addAttribute("alreadyApplied", alreadyApplied);
                }
            }
        }
        
        return "jobs/detail";
    }
}
