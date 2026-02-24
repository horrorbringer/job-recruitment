package com.recruitment.controller;

import com.recruitment.dto.RegistrationDTO;
import com.recruitment.model.User;
import com.recruitment.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JobSeekerService jobSeekerService;
    private final RecruiterService recruiterService;

    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("registration", new RegistrationDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@Valid @ModelAttribute("registration") RegistrationDTO dto,
                          BindingResult result,
                          RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            return "auth/register";
        }

        if (!dto.getPassword().equals(dto.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "error.match", "Passwords do not match");
            return "auth/register";
        }

        try {
            User.Role role = User.Role.valueOf(dto.getRole().toUpperCase());
            User user = userService.createUser(dto.getEmail(), dto.getPassword(), role);

            if (role == User.Role.JOB_SEEKER) {
                // Basic profile will be created when user completes their profile
            } else if (role == User.Role.RECRUITER) {
                // Basic profile will be created when user completes their profile
            }

            redirectAttributes.addFlashAttribute("success", "Registration successful! Please login.");
            return "redirect:/login";
        } catch (Exception e) {
            result.rejectValue("email", "error.email", e.getMessage());
            return "auth/register";
        }
    }
}
