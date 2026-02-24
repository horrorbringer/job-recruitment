package com.recruitment.service;

import com.recruitment.model.*;
import com.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final JobRepository jobRepository;
    private final ApplicationRepository applicationRepository;
    private final CategoryRepository categoryRepository;

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalJobSeekers", userRepository.countByRole(User.Role.JOB_SEEKER));
        stats.put("totalRecruiters", userRepository.countByRole(User.Role.RECRUITER));
        stats.put("totalJobs", jobRepository.count());
        stats.put("approvedJobs", jobRepository.countByStatus(Job.Status.APPROVED));
        stats.put("pendingJobs", jobRepository.countByStatus(Job.Status.PENDING));
        stats.put("totalApplications", applicationRepository.count());
        
        return stats;
    }

    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public void toggleUserStatus(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled());
        userRepository.save(user);
    }

    @Transactional
    public void toggleUserLock(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        user.setAccountNonLocked(!user.isAccountNonLocked());
        userRepository.save(user);
    }

    public Page<Job> getPendingJobs(Pageable pageable) {
        return jobRepository.findByStatusOrderByCreatedAtDesc(Job.Status.PENDING, pageable);
    }

    public Page<Job> getJobsByStatus(Job.Status status, Pageable pageable) {
        return jobRepository.findByStatusOrderByCreatedAtDesc(status, pageable);
    }

    public Map<String, Object> getJobStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", jobRepository.count());
        stats.put("approved", jobRepository.countByStatus(Job.Status.APPROVED));
        stats.put("pending", jobRepository.countByStatus(Job.Status.PENDING));
        stats.put("rejected", jobRepository.countByStatus(Job.Status.REJECTED));
        return stats;
    }

    @Transactional
    public void approveJob(Long jobId) {
        updateJobStatus(jobId, Job.Status.APPROVED);
    }

    @Transactional
    public void rejectJob(Long jobId) {
        updateJobStatus(jobId, Job.Status.REJECTED);
    }

    private void updateJobStatus(Long jobId, Job.Status status) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Transactional
    public Category createCategory(String name, String description) {
        if (categoryRepository.existsByNameIgnoreCase(name)) {
            throw new RuntimeException("Category already exists");
        }
        
        Category category = new Category();
        category.setName(name);
        category.setDescription(description);
        category.setActive(true);
        
        return categoryRepository.save(category);
    }

    @Transactional
    public Category updateCategory(Long id, String name, String description, boolean active) {
        Category category = categoryRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Category not found"));
        
        category.setName(name);
        category.setDescription(description);
        category.setActive(active);
        
        return categoryRepository.save(category);
    }

    @Transactional
    public void deleteCategory(Long id) {
        categoryRepository.deleteById(id);
    }
}
