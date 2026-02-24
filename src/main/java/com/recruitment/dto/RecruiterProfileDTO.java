package com.recruitment.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RecruiterProfileDTO {
    
    @NotBlank(message = "Company name is required")
    private String companyName;
    
    private String companyDescription;
    private String companyWebsite;
    private String companyLocation;
}
