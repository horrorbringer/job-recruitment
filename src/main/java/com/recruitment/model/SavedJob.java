package com.recruitment.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "saved_jobs", uniqueConstraints = @UniqueConstraint(columnNames = { "job_id", "job_seeker_id" }))
@Getter
@Setter
@NoArgsConstructor
public class SavedJob extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    public SavedJob(Job job, JobSeeker jobSeeker) {
        this.job = job;
        this.jobSeeker = jobSeeker;
    }
}
