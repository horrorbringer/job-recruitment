package com.recruitment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applications", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"job_id", "job_seeker_id"}))
@Getter
@Setter
@NoArgsConstructor
public class Application extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_seeker_id", nullable = false)
    private JobSeeker jobSeeker;

    @Column(columnDefinition = "TEXT")
    private String coverLetter;

    private String resumePath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    private LocalDateTime statusUpdatedAt;

    private String statusNote;

    private LocalDateTime interviewDateTime;

    private String interviewLocation;

    @Column(columnDefinition = "TEXT")
    private String interviewNotes;

    public enum Status {
        APPLIED, VIEWED, SHORTLISTED, INTERVIEW_SCHEDULED, 
        INTERVIEWED, OFFERED, HIRED, REJECTED, WITHDRAWN
    }
}
