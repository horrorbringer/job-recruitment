package com.recruitment.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private boolean enabled;

    private boolean accountNonLocked;

    private int failedLoginAttempts = 0;

    private LocalDateTime lockedUntil;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private JobSeeker jobSeeker;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Recruiter recruiter;

    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = false;
        this.accountNonLocked = true;
        this.failedLoginAttempts = 0;
    }

    public boolean isAccountLocked() {
        return !accountNonLocked || (lockedUntil != null && LocalDateTime.now().isBefore(lockedUntil));
    }

    public void incrementFailedAttempts() {
        this.failedLoginAttempts++;
    }

    public void resetFailedAttempts() {
        this.failedLoginAttempts = 0;
    }

    public void lockAccount(int minutes) {
        this.accountNonLocked = false;
        this.lockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
        this.lockedUntil = null;
        this.failedLoginAttempts = 0;
    }

    public String getFullName() {
        if (jobSeeker != null) {
            return jobSeeker.getFullName();
        }
        if (recruiter != null) {
            return recruiter.getCompanyName();
        }
        return email; // Fallback to email for admins or users without profiles
    }

    public enum Role {
        JOB_SEEKER, RECRUITER, ADMIN
    }
}
