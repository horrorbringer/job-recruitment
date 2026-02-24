package com.recruitment.controller;

import com.recruitment.model.*;
import com.recruitment.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getDashboardStats());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(@RequestParam(defaultValue = "0") int page,
                       Model model) {
        Page<User> users = adminService.getAllUsers(PageRequest.of(page, 20, Sort.by("createdAt").descending()));
        model.addAttribute("users", users);
        return "admin/users";
    }

    @PostMapping("/users/{id}/toggle-status")
    public String toggleUserStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleUserStatus(id);
            redirectAttributes.addFlashAttribute("success", "User status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PostMapping("/users/{id}/toggle-lock")
    public String toggleUserLock(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.toggleUserLock(id);
            redirectAttributes.addFlashAttribute("success", "User lock status updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @GetMapping("/jobs")
    public String jobs(@RequestParam(defaultValue = "0") int page,
                      @RequestParam(required = false) String status,
                      Model model) {
        Page<Job> jobs;
        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        
        if (status == null || status.equals("PENDING")) {
            jobs = adminService.getPendingJobs(pageRequest);
        } else if (status.equals("APPROVED")) {
            jobs = adminService.getJobsByStatus(Job.Status.APPROVED, pageRequest);
        } else if (status.equals("REJECTED")) {
            jobs = adminService.getJobsByStatus(Job.Status.REJECTED, pageRequest);
        } else {
            jobs = adminService.getPendingJobs(pageRequest);
        }
        
        model.addAttribute("jobs", jobs);
        model.addAttribute("currentStatus", status);
        model.addAttribute("stats", adminService.getJobStats());
        return "admin/jobs";
    }

    @PostMapping("/jobs/{id}/approve")
    public String approveJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.approveJob(id);
            redirectAttributes.addFlashAttribute("success", "Job approved!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @PostMapping("/jobs/{id}/reject")
    public String rejectJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.rejectJob(id);
            redirectAttributes.addFlashAttribute("success", "Job rejected!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @PostMapping("/jobs/{id}/delete")
    public String deleteJob(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteJob(id);
            redirectAttributes.addFlashAttribute("success", "Job deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/jobs";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        model.addAttribute("categories", adminService.getAllCategories());
        return "admin/categories";
    }

    @PostMapping("/categories")
    public String createCategory(@RequestParam String name,
                                @RequestParam(required = false) String description,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.createCategory(name, description);
            redirectAttributes.addFlashAttribute("success", "Category created!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}")
    public String updateCategory(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) String description,
                                @RequestParam(defaultValue = "true") boolean active,
                                RedirectAttributes redirectAttributes) {
        try {
            adminService.updateCategory(id, name, description, active);
            redirectAttributes.addFlashAttribute("success", "Category updated!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/categories/{id}/delete")
    public String deleteCategory(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            adminService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("success", "Category deleted!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/admin/categories";
    }
}
