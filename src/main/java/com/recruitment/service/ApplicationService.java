package com.recruitment.service;

import com.recruitment.dto.ApplicationCreateDTO;
import com.recruitment.model.*;
import com.recruitment.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;
    private final NotificationService notificationService;

    @Transactional
    public Application apply(Long jobSeekerId, Long jobId, ApplicationCreateDTO dto) {
        Job job = jobRepository.findById(jobId)
            .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != Job.Status.APPROVED) {
            throw new RuntimeException("This job is not available for applications");
        }

        if (job.getDeadline() != null && job.getDeadline().isBefore(java.time.LocalDate.now())) {
            throw new RuntimeException("Application deadline has passed");
        }

        if (applicationRepository.existsByJobIdAndJobSeekerId(jobId, jobSeekerId)) {
            throw new RuntimeException("You have already applied for this job");
        }

        JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        Application application = new Application();
        application.setJob(job);
        application.setJobSeeker(jobSeeker);
        application.setCoverLetter(dto.getCoverLetter());
        application.setResumePath(jobSeeker.getResume());
        application.setStatus(Application.Status.APPLIED);
        application.setStatusUpdatedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    public List<Application> getJobSeekerApplications(Long jobSeekerId) {
        return applicationRepository.findByJobSeekerIdOrderByCreatedAtDesc(jobSeekerId);
    }

    public Page<Application> getJobApplications(Long jobId, Pageable pageable) {
        return applicationRepository.findByJobIdOrderByCreatedAtDesc(jobId, pageable);
    }

    @Transactional
    public void updateStatus(Long applicationId, Application.Status status, String note) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(status);
        application.setStatusNote(note);
        application.setStatusUpdatedAt(LocalDateTime.now());

        applicationRepository.save(application);

        User jobSeekerUser = application.getJobSeeker().getUser();
        String jobTitle = application.getJob().getTitle();
        
        String type = status.name().toLowerCase();
        String link = "/job-seeker/applications";
        
        notificationService.createNotification(
            jobSeekerUser,
            "Application Status Updated",
            "Your application for " + jobTitle + " has been " + status.name().replace("_", " ").toLowerCase(),
            type,
            link
        );
    }

    @Transactional
    public void scheduleInterview(Long applicationId, LocalDateTime interviewDateTime, 
                                  String interviewLocation, String interviewNotes) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        application.setStatus(Application.Status.INTERVIEW_SCHEDULED);
        application.setInterviewDateTime(interviewDateTime);
        application.setInterviewLocation(interviewLocation);
        application.setInterviewNotes(interviewNotes);
        application.setStatusUpdatedAt(LocalDateTime.now());

        applicationRepository.save(application);

        User jobSeekerUser = application.getJobSeeker().getUser();
        String jobTitle = application.getJob().getTitle();
        
        notificationService.createNotification(
            jobSeekerUser,
            "Interview Scheduled!",
            "Your interview for " + jobTitle + " has been scheduled",
            "interview",
            "/job-seeker/applications"
        );
    }

    @Transactional
    public void withdraw(Long applicationId, Long jobSeekerId) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJobSeeker().getId().equals(jobSeekerId)) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatus(Application.Status.WITHDRAWN);
        application.setStatusUpdatedAt(LocalDateTime.now());
        applicationRepository.save(application);
    }

    @Transactional
    public void requestReschedule(Long applicationId, Long jobSeekerId, String reason) {
        Application application = applicationRepository.findById(applicationId)
            .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJobSeeker().getId().equals(jobSeekerId)) {
            throw new RuntimeException("Unauthorized");
        }

        application.setStatusNote("Reschedule requested: " + reason);
        applicationRepository.save(application);

        User recruiterUser = application.getJob().getRecruiter().getUser();
        notificationService.createNotification(
            recruiterUser,
            "Interview Reschedule Request",
            "Candidate requested reschedule for " + application.getJob().getTitle(),
            "reschedule",
            "/recruiter/applications"
        );
    }

    public Application getApplication(Long id) {
        return applicationRepository.findById(id).orElse(null);
    }

    public boolean hasApplied(Long jobSeekerId, Long jobId) {
        return applicationRepository.existsByJobIdAndJobSeekerId(jobId, jobSeekerId);
    }

    public long countByJobSeekerId(Long jobSeekerId) {
        return applicationRepository.countByJobSeekerId(jobSeekerId);
    }

    public long countByRecruiterId(Long recruiterId) {
        return applicationRepository.countByRecruiterId(recruiterId);
    }

    public Page<Application> getByRecruiterId(Long recruiterId, Pageable pageable) {
        return applicationRepository.findByJobRecruiterId(recruiterId, pageable);
    }
}
