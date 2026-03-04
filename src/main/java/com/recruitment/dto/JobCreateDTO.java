package com.recruitment.dto;

import com.recruitment.model.Job;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class JobCreateDTO {

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    private String requirements;
    private String responsibilities;
    private String location;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
    private Job.JobType jobType;
    private Job.ExperienceLevel experienceLevel;
    private Long categoryId;
    private LocalDate deadline;
    private Integer vacancies;
}
