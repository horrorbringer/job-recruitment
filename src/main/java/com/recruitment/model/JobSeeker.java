package com.recruitment.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_seekers")
@Getter
@Setter
@NoArgsConstructor
public class JobSeeker extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String fullName;

    private String phone;

    @Column(columnDefinition = "TEXT")
    private String about;

    private String profilePicture;

    private String resume;

    private Integer experienceYears;

    @Column(columnDefinition = "TEXT")
    private String education;

    @Column(columnDefinition = "TEXT")
    private String skills;

    private String location;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    public enum ExperienceLevel {
        ENTRY_LEVEL, MID_LEVEL, SENIOR, EXECUTIVE
    }
}
