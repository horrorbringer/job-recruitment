package com.recruitment.controller;

import com.recruitment.dto.*;
import com.recruitment.model.*;
import com.recruitment.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

@Controller
@RequestMapping("/job-seeker")
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final ApplicationService applicationService;
    private final UserService userService;
    private final JobService jobService;
    private final SavedJobService savedJobService;
    private final PdfResumeService pdfResumeService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        model.addAttribute("profile", profile);
        if (profile != null) {
            model.addAttribute("applicationCount", applicationService.countByJobSeekerId(profile.getId()));
            model.addAttribute("recentApplications", applicationService.getRecentApplications(profile.getId(), 5));
            model.addAttribute("recommendedJobs",
                    jobService.getRecommendedJobs(profile.getSkills(), PageRequest.of(0, 5)).getContent());
            model.addAttribute("interviewCount", applicationService.countByJobSeekerIdAndStatus(profile.getId(),
                    Application.Status.INTERVIEW_SCHEDULED));
            model.addAttribute("shortlistedCount",
                    applicationService.countByJobSeekerIdAndStatus(profile.getId(), Application.Status.SHORTLISTED));
            model.addAttribute("profileViews", 0);
            model.addAttribute("savedJobsCount", savedJobService.getSavedJobs(profile.getId()).size());
            model.addAttribute("recentSavedJobs",
                    savedJobService.getSavedJobs(profile.getId()).stream().limit(3).toList());
            model.addAttribute("profileStrength", jobSeekerService.calculateProfileStrength(profile));
        } else {
            model.addAttribute("applicationCount", 0);
            model.addAttribute("recentApplications", java.util.Collections.emptyList());
            model.addAttribute("interviewCount", 0);
            model.addAttribute("shortlistedCount", 0);
            model.addAttribute("profileViews", 0);
            model.addAttribute("savedJobsCount", 0);
            model.addAttribute("profileStrength", 0);
        }

        return "job-seeker/dashboard";
    }

    @GetMapping("/profile")
    public String profileForm(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        if (profile == null) {
            model.addAttribute("profileDTO", new JobSeekerProfileDTO());
        } else {
            JobSeekerProfileDTO dto = new JobSeekerProfileDTO();
            dto.setFullName(profile.getFullName());
            dto.setPhone(profile.getPhone());
            dto.setAbout(profile.getAbout());
            dto.setSkills(profile.getSkills());
            dto.setEducation(profile.getEducation());
            dto.setLocation(profile.getLocation());
            dto.setExperienceYears(profile.getExperienceYears());
            dto.setExperienceLevel(profile.getExperienceLevel());
            model.addAttribute("profileDTO", dto);
            model.addAttribute("profile", profile);
        }

        return "job-seeker/profile";
    }

    @PostMapping("/profile")
    public String updateProfile(@AuthenticationPrincipal UserDetails userDetails,
            @Valid @ModelAttribute("profileDTO") JobSeekerProfileDTO dto,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "job-seeker/profile";
        }

        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker existing = jobSeekerService.getProfile(user.getId());

        try {
            if (existing == null) {
                jobSeekerService.createProfile(user.getId(), dto);
            } else {
                jobSeekerService.updateProfile(user.getId(), dto);
            }
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job-seeker/profile";
    }

    @PostMapping("/profile/resume")
    public String uploadResume(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("resume") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            jobSeekerService.uploadResume(user.getId(), file);
            redirectAttributes.addFlashAttribute("success", "Resume uploaded successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job-seeker/profile";
    }

    @GetMapping("/resume/generate")
    public ResponseEntity<byte[]> generateResume(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        if (profile == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            byte[] pdfBytes = pdfResumeService.generateResumeBytes(profile);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Suggest a nice filename
            String filename = profile.getFullName().replaceAll("\\s+", "_") + "_Resume.pdf";
            headers.setContentDispositionFormData("attachment", filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/profile/picture")
    public String uploadPicture(@AuthenticationPrincipal UserDetails userDetails,
            @RequestParam("picture") MultipartFile file,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            jobSeekerService.uploadProfilePicture(user.getId(), file);
            redirectAttributes.addFlashAttribute("success", "Profile picture updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job-seeker/profile";
    }

    @GetMapping("/applications")
    public String applications(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        if (profile != null) {
            model.addAttribute("applications", applicationService.getJobSeekerApplications(profile.getId()));
        }

        return "job-seeker/applications";
    }

    @GetMapping("/saved-jobs")
    public String savedJobs(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        if (profile != null) {
            model.addAttribute("savedJobs", savedJobService.getSavedJobs(profile.getId()));
        }

        return "job-seeker/saved-jobs";
    }

    @PostMapping("/saved-jobs/toggle/{jobId}")
    @ResponseBody
    public String toggleSaveJob(@PathVariable Long jobId, @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());

        if (profile == null)
            return "error";

        if (savedJobService.isJobSaved(profile.getId(), jobId)) {
            savedJobService.unsaveJob(profile.getId(), jobId);
            return "removed";
        } else {
            savedJobService.saveJob(profile.getId(), jobId);
            return "saved";
        }
    }

    @GetMapping("/apply/{jobId}")
    public String apply(@PathVariable Long jobId) {
        return "redirect:/jobs/" + jobId;
    }

    @PostMapping("/apply/{jobId}")
    public String apply(@PathVariable Long jobId,
            @AuthenticationPrincipal UserDetails userDetails,
            @ModelAttribute ApplicationCreateDTO dto,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            JobSeeker profile = jobSeekerService.getProfile(user.getId());

            if (profile == null) {
                redirectAttributes.addFlashAttribute("error", "Please complete your profile first");
                return "redirect:/job-seeker/profile";
            }

            applicationService.apply(profile.getId(), jobId, dto);
            redirectAttributes.addFlashAttribute("success", "Application submitted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/jobs/" + jobId;
    }

    @PostMapping("/applications/{id}/withdraw")
    public String withdraw(@PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            JobSeeker profile = jobSeekerService.getProfile(user.getId());
            applicationService.withdraw(id, profile.getId());
            redirectAttributes.addFlashAttribute("success", "Application withdrawn");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job-seeker/applications";
    }

    @PostMapping("/applications/{id}/reschedule")
    public String requestReschedule(@PathVariable Long id,
            @RequestParam String reason,
            @AuthenticationPrincipal UserDetails userDetails,
            RedirectAttributes redirectAttributes) {

        try {
            User user = userService.findByEmail(userDetails.getUsername());
            JobSeeker profile = jobSeekerService.getProfile(user.getId());
            applicationService.requestReschedule(id, profile.getId(), reason);
            redirectAttributes.addFlashAttribute("success", "Reschedule request sent to recruiter");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }

        return "redirect:/job-seeker/applications";
    }
}
