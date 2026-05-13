package com.recruitment.controller;

import com.recruitment.dto.*;
import com.recruitment.model.*;
import com.recruitment.repository.CategoryRepository;
import com.recruitment.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    private final RecruiterService recruiterService;
    private final JobService jobService;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final CategoryRepository categoryRepository;

    @GetMapping("/dashboard")
    public String dashboard(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Recruiter profile = recruiterService.getProfile(user.getId());
        long jobCount = jobService.countByRecruiterId(user.getId());
        long applicationCount = profile != null ? applicationService.countByRecruiterId(profile.getId()) : 0;
        long interviewCount = profile != null
                ? applicationService.countByRecruiterIdAndStatus(profile.getId(),
                        Application.Status.INTERVIEW_SCHEDULED)
                : 0;
        long openPositions = profile != null
                ? jobService.countByRecruiterIdAndStatus(profile.getId(), Job.Status.APPROVED)
                : 0;

        model.addAttribute("profile", profile);
        model.addAttribute("jobCount", jobCount);
        model.addAttribute("applicationCount", applicationCount);
        model.addAttribute("interviewCount", interviewCount);
        model.addAttribute("openPositions", openPositions);

        if (profile != null) {
            Page<Application> recentApps = applicationService.getByRecruiterId(
                    profile.getId(), PageRequest.of(0, 5, Sort.by("createdAt").descending()));
            model.addAttribute("recentApplications", recentApps);
        }

        return "recruiter/dashboard";
    }

    @GetMapping("/applications")
    public String allApplications(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "100") int size,
            Model model,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Recruiter profile = recruiterService.getProfile(user.getId());
        if (profile == null) {
            return "redirect:/recruiter/profile";
        }

        Page<Application> applications = applicationService.getByRecruiterId(
                profile.getId(), PageRequest.of(page, size, Sort.by("createdAt").descending()));

        java.util.List<Application> appList = applications.getContent();
        model.addAttribute("applications", appList);

        // Per-column counts for Kanban empty-state rendering (Thymeleaf doesn't support
        // lambdas)
        model.addAttribute("appliedCount",
                appList.stream().filter(
                        a -> a.getStatus() == Application.Status.APPLIED || a.getStatus() == Application.Status.VIEWED)
                        .count());
        model.addAttribute("reviewCount",
                appList.stream().filter(a -> a.getStatus() == Application.Status.SHORTLISTED).count());
        model.addAttribute("interviewCount",
                appList.stream().filter(a -> a.getStatus() == Application.Status.INTERVIEW_SCHEDULED
                        || a.getStatus() == Application.Status.INTERVIEWED).count());
        model.addAttribute("hiredCount",
                appList.stream().filter(
                        a -> a.getStatus() == Application.Status.OFFERED || a.getStatus() == Application.Status.HIRED)
                        .count());
        model.addAttribute("rejectedCount", appList.stream().filter(
                a -> a.getStatus() == Application.Status.REJECTED || a.getStatus() == Application.Status.WITHDRAWN)
                .count());

        return "recruiter/applications";
    }

    @GetMapping("/profile")
    public String profileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Recruiter profile = recruiterService.getProfile(user.getId());

        if (profile == null) {
            model.addAttribute("profileDTO", new RecruiterProfileDTO());
        } else {
            RecruiterProfileDTO dto = new RecruiterProfileDTO();
            dto.setCompanyName(profile.getCompanyName());
            dto.setCompanyDescription(profile.getCompanyDescription());
            dto.setCompanyWebsite(profile.getCompanyWebsite());
            dto.setCompanyLocation(profile.getCompanyLocation());
            model.addAttribute("profileDTO", dto);
            model.addAttribute("profile", profile);
        }

        return "recruiter/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("profileDTO") RecruiterProfileDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "recruiter/profile";
        }

        User user = userService.findByEmail(userDetails.getUsername());

        try {
            Recruiter existing = recruiterService.getProfile(user.getId());
            if (existing == null) {
                recruiterService.createProfile(user.getId(), dto);
            } else {
                recruiterService.updateProfile(user.getId(), dto);
            }
            redirectAttributes.addFlashAttribute("success", "Company profile updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recruiter/profile";
    }

    @PostMapping("/profile/logo")
    public String uploadLogo(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("logo") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            recruiterService.uploadLogo(user.getId(), file);
            redirectAttributes.addFlashAttribute("success", "Logo uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recruiter/profile";
    }

    @GetMapping("/jobs")
    public String jobs(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        User user = userService.findByEmail(userDetails.getUsername());
        Recruiter profile = recruiterService.getProfile(user.getId());

        if (profile != null) {
            Page<Job> jobs = jobService.getRecruiterJobs(profile.getId(),
                    PageRequest.of(page, 10, Sort.by("createdAt").descending()));
            model.addAttribute("jobs", jobs);
        }
        return "recruiter/jobs";
    }

    @GetMapping("/jobs/create")
    public String createJobForm(Model model) {
        model.addAttribute("jobDTO", new JobCreateDTO());
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
        return "recruiter/job-form";
    }

    @PostMapping("/jobs/create")
    public String createJob(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("jobDTO") JobCreateDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
            return "recruiter/job-form";
        }

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            Recruiter profile = recruiterService.getProfile(user.getId());
            if (profile != null) {
                jobService.createJob(profile.getId(), dto);
                redirectAttributes.addFlashAttribute("success", "Job posted successfully! Pending approval.");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/jobs/{id}/edit")
    public String editJobForm(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        Job job = jobService.getJob(id);
        if (job == null) {
            return "redirect:/recruiter/jobs";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        Recruiter profile = recruiterService.getProfile(user.getId());
        if (profile == null || !job.getRecruiter().getId().equals(profile.getId())) {
            return "redirect:/recruiter/jobs";
        }

        JobCreateDTO dto = new JobCreateDTO();
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setRequirements(job.getRequirements());
        dto.setResponsibilities(job.getResponsibilities());
        dto.setLocation(job.getLocation());
        dto.setSalaryMin(job.getSalaryMin());
        dto.setSalaryMax(job.getSalaryMax());
        dto.setJobType(job.getJobType());
        dto.setExperienceLevel(job.getExperienceLevel());
        dto.setCategoryId(job.getCategory() != null ? job.getCategory().getId() : null);
        dto.setDeadline(job.getDeadline());
        dto.setVacancies(job.getVacancies());

        model.addAttribute("jobDTO", dto);
        model.addAttribute("jobId", id);
        model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());

        return "recruiter/job-form";
    }

    @PostMapping("/jobs/{id}/edit")
    public String updateJob(@PathVariable Long id,
            @Valid @ModelAttribute("jobDTO") JobCreateDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (result.hasErrors()) {
            model.addAttribute("jobId", id);
            model.addAttribute("categories", categoryRepository.findByActiveTrueOrderByNameAsc());
            return "recruiter/job-form";
        }

        try {
            jobService.updateJob(id, dto);
            redirectAttributes.addFlashAttribute("success", "Job updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recruiter/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id,
            RedirectAttributes redirectAttributes) {
        try {
            jobService.deleteJob(id);
            redirectAttributes.addFlashAttribute("success", "Job deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/recruiter/jobs";
    }

    @GetMapping("/jobs/{id}/applications")
    public String applications(@PathVariable Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model) {

        Job job = jobService.getJob(id);
        Page<Application> applications = applicationService.getJobApplications(id,
                PageRequest.of(page, 100, Sort.by("createdAt").descending()));

        model.addAttribute("job", job);
        model.addAttribute("applications", applications.getContent());

        return "recruiter/applications";
    }

    @PostMapping("/applications/{id}/status")
    public String updateApplicationStatus(@PathVariable Long id,
            @RequestParam Application.Status status,
            @RequestParam(required = false) String note,
            @RequestParam(required = false) Long jobId,
            RedirectAttributes redirectAttributes) {

        try {
            applicationService.updateStatus(id, status, note);
            redirectAttributes.addFlashAttribute("success", "Application status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        if (jobId != null) {
            return "redirect:/recruiter/jobs/" + jobId + "/applications";
        }
        return "redirect:/recruiter/applications";
    }

    @PostMapping("/applications/{id}/status-ajax")
    @ResponseBody
    public org.springframework.http.ResponseEntity<?> updateApplicationStatusAjax(@PathVariable Long id,
            @RequestParam Application.Status status) {
        try {
            applicationService.updateStatus(id, status, null);
            return org.springframework.http.ResponseEntity
                    .ok(java.util.Map.of("message", "Status updated", "status", status.name()));
        } catch (Exception e) {
            return org.springframework.http.ResponseEntity.badRequest().body(java.util.Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/applications/{id}/schedule-interview")
    public String scheduleInterview(@PathVariable Long id,
            @RequestParam String interviewDate,
            @RequestParam String interviewTime,
            @RequestParam String interviewLocation,
            @RequestParam(required = false) String interviewNotes,
            RedirectAttributes redirectAttributes) {

        try {
            LocalDateTime dateTime = LocalDateTime.parse(interviewDate + "T" + interviewTime);
            applicationService.scheduleInterview(id, dateTime, interviewLocation, interviewNotes);
            redirectAttributes.addFlashAttribute("success", "Interview scheduled successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to schedule interview: " + e.getMessage());
        }

        return "redirect:/recruiter/applications";
    }
}
