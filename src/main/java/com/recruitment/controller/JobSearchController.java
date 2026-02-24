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

    @GetMapping("/jobs/search")
    public String search(@ModelAttribute JobSearchDTO search,
                        @RequestParam(defaultValue = "0") int page,
                        @RequestParam(defaultValue = "12") int size,
                        Model model) {
        
        Page<Job> jobs = jobService.searchJobs(search, 
            PageRequest.of(page, size, Sort.by("createdAt").descending()));
        
        model.addAttribute("jobs", jobs);
        model.addAttribute("search", search);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        
        return "jobs/search";
    }
}
