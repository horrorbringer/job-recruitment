package com.recruitment.dto;

import com.recruitment.model.JobSeeker;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobSeekerProfileDTO {
    
    @NotBlank(message = "Full name is required")
    private String fullName;
    
    private String phone;
    private String about;
    private String skills;
    private String education;
    private String location;
    private Integer experienceYears;
    private JobSeeker.ExperienceLevel experienceLevel;
}
