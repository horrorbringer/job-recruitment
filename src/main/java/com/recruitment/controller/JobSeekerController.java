package com.recruitment.controller;

import com.recruitment.dto.*;
import com.recruitment.model.*;
import com.recruitment.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/job-seeker")
@RequiredArgsConstructor
public class JobSeekerController {

    private final JobSeekerService jobSeekerService;
    private final ApplicationService applicationService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        JobSeeker profile = jobSeekerService.getProfile(user.getId());
        
        model.addAttribute("profile", profile);
        if (profile != null) {
            model.addAttribute("applicationCount", applicationService.countByJobSeekerId(profile.getId()));
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
