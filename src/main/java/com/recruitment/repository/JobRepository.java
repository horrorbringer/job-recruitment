package com.recruitment.repository;

import com.recruitment.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    
    Page<Job> findByStatusOrderByCreatedAtDesc(Job.Status status, Pageable pageable);
    
    Page<Job> findByRecruiterIdOrderByCreatedAtDesc(Long recruiterId, Pageable pageable);
    
    @Query("SELECT j FROM Job j WHERE j.status = 'APPROVED' AND " +
           "(:keyword IS NULL OR j.title LIKE %:keyword% OR j.description LIKE %:keyword%) AND " +
           "(:location IS NULL OR j.location LIKE %:location%) AND " +
           "(:categoryId IS NULL OR j.category.id = :categoryId) AND " +
           "(:jobType IS NULL OR j.jobType = :jobType) AND " +
           "(:salaryMin IS NULL OR j.salaryMax >= :salaryMin) AND " +
           "(:salaryMax IS NULL OR j.salaryMin <= :salaryMax) AND " +
           "(j.deadline IS NULL OR j.deadline >= :today)")
    Page<Job> searchJobs(@Param("keyword") String keyword,
                        @Param("location") String location,
                        @Param("categoryId") Long categoryId,
                        @Param("jobType") Job.JobType jobType,
                        @Param("salaryMin") BigDecimal salaryMin,
                        @Param("salaryMax") BigDecimal salaryMax,
                        @Param("today") LocalDate today,
                        Pageable pageable);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.status = :status")
    long countByStatus(Job.Status status);
    
    List<Job> findByDeadlineBeforeAndStatusNot(LocalDate date, Job.Status status);

    @Query(value = "SELECT * FROM jobs WHERE status = 'APPROVED' AND " +
           "(title ~* :skillRegex OR description ~* :skillRegex OR requirements ~* :skillRegex) " +
           "ORDER BY created_at DESC", 
           countQuery = "SELECT count(*) FROM jobs WHERE status = 'APPROVED' AND " +
           "(title ~* :skillRegex OR description ~* :skillRegex OR requirements ~* :skillRegex)",
           nativeQuery = true)
    Page<Job> findBySkillsRegex(@Param("skillRegex") String skillRegex, Pageable pageable);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.recruiter.id = :recruiterId")
    long countByRecruiterId(@Param("recruiterId") Long recruiterId);
}
