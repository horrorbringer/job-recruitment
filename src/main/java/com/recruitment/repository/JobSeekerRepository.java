package com.recruitment.repository;

import com.recruitment.model.JobSeeker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobSeekerRepository extends JpaRepository<JobSeeker, Long> {
    Optional<JobSeeker> findByUserId(Long userId);
    List<JobSeeker> findBySkillsContainingIgnoreCase(String skill);
    
    @Query("SELECT js FROM JobSeeker js WHERE " +
           "LOWER(js.skills) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(js.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<JobSeeker> searchByKeyword(String keyword);
}
