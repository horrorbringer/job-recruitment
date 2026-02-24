package com.recruitment.scheduler;

import com.recruitment.model.Job;
import com.recruitment.repository.JobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JobExpiryScheduler {

    private final JobRepository jobRepository;

    @Scheduled(cron = "0 0 0 * * ?")
    public void expireJobs() {
        log.info("Running job expiry scheduler...");
        
        List<Job> expiredJobs = jobRepository.findByDeadlineBeforeAndStatusNot(
            LocalDate.now(), 
            Job.Status.CLOSED
        );
        
        for (Job job : expiredJobs) {
            job.setStatus(Job.Status.CLOSED);
            jobRepository.save(job);
            log.info("Job expired: {} - {}", job.getId(), job.getTitle());
        }
        
        log.info("Expired {} jobs", expiredJobs.size());
    }
}
