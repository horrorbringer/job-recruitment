package com.recruitment.service;

import com.recruitment.model.Job;
import com.recruitment.model.JobSeeker;
import com.recruitment.model.SavedJob;
import com.recruitment.repository.JobRepository;
import com.recruitment.repository.JobSeekerRepository;
import com.recruitment.repository.SavedJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SavedJobService {

    private final SavedJobRepository savedJobRepository;
    private final JobRepository jobRepository;
    private final JobSeekerRepository jobSeekerRepository;

    @Transactional
    public void saveJob(Long jobSeekerId, Long jobId) {
        if (!savedJobRepository.existsByJobSeekerIdAndJobId(jobSeekerId, jobId)) {
            Job job = jobRepository.findById(jobId)
                    .orElseThrow(() -> new RuntimeException("Job not found"));
            JobSeeker jobSeeker = jobSeekerRepository.findById(jobSeekerId)
                    .orElseThrow(() -> new RuntimeException("Job seeker not found"));

            SavedJob savedJob = new SavedJob(job, jobSeeker);
            savedJobRepository.save(savedJob);
        }
    }

    @Transactional
    public void unsaveJob(Long jobSeekerId, Long jobId) {
        savedJobRepository.deleteByJobSeekerIdAndJobId(jobSeekerId, jobId);
    }

    public List<Job> getSavedJobs(Long jobSeekerId) {
        return savedJobRepository.findByJobSeekerIdOrderByCreatedAtDesc(jobSeekerId)
                .stream()
                .map(SavedJob::getJob)
                .collect(Collectors.toList());
    }

    public boolean isJobSaved(Long jobSeekerId, Long jobId) {
        return savedJobRepository.existsByJobSeekerIdAndJobId(jobSeekerId, jobId);
    }
}
