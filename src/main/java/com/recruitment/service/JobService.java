package com.recruitment.service;

import com.recruitment.dto.JobCreateDTO;
import com.recruitment.dto.JobSearchDTO;
import com.recruitment.model.Job;
import com.recruitment.model.Recruiter;
import com.recruitment.repository.CategoryRepository;
import com.recruitment.repository.JobRepository;
import com.recruitment.repository.RecruiterRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobService {

    private final JobRepository jobRepository;
    private final RecruiterRepository recruiterRepository;
    private final CategoryRepository categoryRepository;

    @Transactional
    public Job createJob(Long recruiterId, JobCreateDTO dto) {
        Recruiter recruiter = recruiterRepository.findById(recruiterId)
            .orElseThrow(() -> new RuntimeException("Recruiter not found"));

        Job job = new Job();
        job.setRecruiter(recruiter);
        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setDeadline(dto.getDeadline());
        job.setVacancies(dto.getVacancies());
        job.setStatus(Job.Status.PENDING);

        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId())
                .ifPresent(job::setCategory);
        }

        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long jobId, JobCreateDTO dto) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        job.setTitle(dto.getTitle());
        job.setDescription(dto.getDescription());
        job.setRequirements(dto.getRequirements());
        job.setLocation(dto.getLocation());
        job.setSalaryMin(dto.getSalaryMin());
        job.setSalaryMax(dto.getSalaryMax());
        job.setJobType(dto.getJobType());
        job.setExperienceLevel(dto.getExperienceLevel());
        job.setDeadline(dto.getDeadline());
        job.setVacancies(dto.getVacancies());

        if (dto.getCategoryId() != null) {
            categoryRepository.findById(dto.getCategoryId())
                .ifPresent(job::setCategory);
        }

        return jobRepository.save(job);
    }

    public Page<Job> searchJobs(JobSearchDTO search, Pageable pageable) {
        return jobRepository.searchJobs(
            search.getKeyword(),
            search.getLocation(),
            search.getCategoryId(),
            search.getJobType(),
            search.getSalaryMin(),
            search.getSalaryMax(),
            LocalDate.now(),
            pageable
        );
    }

    public Page<Job> getApprovedJobs(Pageable pageable) {
        return jobRepository.findByStatusOrderByCreatedAtDesc(Job.Status.APPROVED, pageable);
    }

    public Page<Job> getRecruiterJobs(Long recruiterId, Pageable pageable) {
        return jobRepository.findByRecruiterIdOrderByCreatedAtDesc(recruiterId, pageable);
    }

    public Job getJob(Long id) {
        return jobRepository.findById(id).orElse(null);
    }

    @Transactional
    public void updateStatus(Long jobId, Job.Status status) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));
        job.setStatus(status);
        jobRepository.save(job);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    public long countByStatus(Job.Status status) {
        return jobRepository.countByStatus(status);
    }

    public long countByRecruiterId(Long recruiterId) {
        return jobRepository.countByRecruiterId(recruiterId);
    }
}
