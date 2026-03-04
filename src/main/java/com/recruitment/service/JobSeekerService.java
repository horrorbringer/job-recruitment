package com.recruitment.service;

import com.recruitment.dto.JobSeekerProfileDTO;
import com.recruitment.model.JobSeeker;
import com.recruitment.model.User;
import com.recruitment.repository.JobSeekerRepository;
import com.recruitment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@Service
@RequiredArgsConstructor
public class JobSeekerService {

    private final JobSeekerRepository jobSeekerRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    @Transactional
    public JobSeeker createProfile(Long userId, JobSeekerProfileDTO dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        JobSeeker profile = new JobSeeker();
        profile.setUser(user);
        profile.setFullName(dto.getFullName());
        profile.setPhone(dto.getPhone());
        profile.setAbout(dto.getAbout());
        profile.setSkills(dto.getSkills());
        profile.setEducation(dto.getEducation());
        profile.setLocation(dto.getLocation());
        profile.setExperienceYears(dto.getExperienceYears());
        profile.setExperienceLevel(dto.getExperienceLevel());

        return jobSeekerRepository.save(profile);
    }

    @Transactional
    public JobSeeker updateProfile(Long userId, JobSeekerProfileDTO dto) {
        JobSeeker profile = jobSeekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        profile.setFullName(dto.getFullName());
        profile.setPhone(dto.getPhone());
        profile.setAbout(dto.getAbout());
        profile.setSkills(dto.getSkills());
        profile.setEducation(dto.getEducation());
        profile.setLocation(dto.getLocation());
        profile.setExperienceYears(dto.getExperienceYears());
        profile.setExperienceLevel(dto.getExperienceLevel());

        return jobSeekerRepository.save(profile);
    }

    @Transactional
    public void uploadResume(Long userId, MultipartFile file) {
        JobSeeker profile = jobSeekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        String resumePath = fileStorageService.storeResume(file, userId);
        profile.setResume(resumePath);
        jobSeekerRepository.save(profile);
    }

    @Transactional
    public void uploadProfilePicture(Long userId, MultipartFile file) {
        JobSeeker profile = jobSeekerRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Profile not found"));

        String picturePath = fileStorageService.storeImage(file, userId, "profile");
        profile.setProfilePicture(picturePath);
        jobSeekerRepository.save(profile);
    }

    public JobSeeker getProfile(Long userId) {
        return jobSeekerRepository.findByUserId(userId).orElse(null);
    }

    public JobSeeker getProfileById(Long id) {
        return jobSeekerRepository.findById(id).orElse(null);
    }

    public int calculateProfileStrength(JobSeeker profile) {
        if (profile == null)
            return 0;
        int strength = 0;
        if (profile.getFullName() != null && !profile.getFullName().isEmpty())
            strength += 15;
        if (profile.getPhone() != null && !profile.getPhone().isEmpty())
            strength += 10;
        if (profile.getAbout() != null && !profile.getAbout().isEmpty())
            strength += 15;
        if (profile.getSkills() != null && !profile.getSkills().isEmpty())
            strength += 15;
        if (profile.getEducation() != null && !profile.getEducation().isEmpty())
            strength += 15;
        if (profile.getLocation() != null && !profile.getLocation().isEmpty())
            strength += 10;
        if (profile.getResume() != null)
            strength += 10;
        if (profile.getProfilePicture() != null)
            strength += 10;
        return strength;
    }
}
