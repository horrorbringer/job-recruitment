package com.recruitment.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegistrationDTO {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @NotBlank(message = "Confirm password is required")
    private String confirmPassword;
    
    @NotBlank(message = "Role is required")
    private String role;
    
    private String fullName;
    private String phone;
    
    private String companyName;
    private String companyWebsite;
    private String companyLocation;
}
