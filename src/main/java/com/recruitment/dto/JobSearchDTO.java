package com.recruitment.dto;

import com.recruitment.model.Job;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class JobSearchDTO {
    private String keyword;
    private String location;
    private Long categoryId;
    private Job.JobType jobType;
    private BigDecimal salaryMin;
    private BigDecimal salaryMax;
}
