package com.recruitment.repository;

import com.recruitment.model.Application;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
    
    List<Application> findByJobSeekerIdOrderByCreatedAtDesc(Long jobSeekerId);
    
    Page<Application> findByJobIdOrderByCreatedAtDesc(Long jobId, Pageable pageable);
    
    Optional<Application> findByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);
    
    boolean existsByJobIdAndJobSeekerId(Long jobId, Long jobSeekerId);
    
    @Query("SELECT a.status, COUNT(a) FROM Application a WHERE a.job.recruiter.id = :recruiterId GROUP BY a.status")
    List<Object[]> countByStatusForRecruiter(@Param("recruiterId") Long recruiterId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.job.recruiter.id = :recruiterId")
    long countByRecruiterId(@Param("recruiterId") Long recruiterId);
    
    @Query("SELECT COUNT(a) FROM Application a WHERE a.jobSeeker.id = :jobSeekerId")
    long countByJobSeekerId(@Param("jobSeekerId") Long jobSeekerId);

    @Query("SELECT a FROM Application a WHERE a.job.recruiter.id = :recruiterId ORDER BY a.createdAt DESC")
    Page<Application> findByJobRecruiterId(@Param("recruiterId") Long recruiterId, Pageable pageable);
}
