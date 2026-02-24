package com.recruitment.service;

import com.recruitment.dto.RecruiterProfileDTO;
import com.recruitment.model.Recruiter;
import com.recruitment.model.User;
import com.recruitment.repository.RecruiterRepository;
import com.recruitment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class RecruiterService {

    private final RecruiterRepository recruiterRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public Recruiter createProfile(Long userId, RecruiterProfileDTO dto) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));

        Recruiter profile = new Recruiter();
        profile.setUser(user);
        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyDescription(dto.getCompanyDescription());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setCompanyLocation(dto.getCompanyLocation());

        return recruiterRepository.save(profile);
    }

    @Transactional
    public Recruiter updateProfile(Long userId, RecruiterProfileDTO dto) {
        Recruiter profile = recruiterRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setCompanyName(dto.getCompanyName());
        profile.setCompanyDescription(dto.getCompanyDescription());
        profile.setCompanyWebsite(dto.getCompanyWebsite());
        profile.setCompanyLocation(dto.getCompanyLocation());

        return recruiterRepository.save(profile);
    }

    @Transactional
    public void uploadLogo(Long userId, MultipartFile file) {
        Recruiter profile = recruiterRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found"));

        String logoPath = fileStorageService.storeImage(file, userId, "logo");
        profile.setCompanyLogo(logoPath);
        recruiterRepository.save(profile);
    }

    public Recruiter getProfile(Long userId) {
        return recruiterRepository.findByUserId(userId).orElse(null);
    }

    public Recruiter getProfileById(Long id) {
        return recruiterRepository.findById(id).orElse(null);
    }
}
