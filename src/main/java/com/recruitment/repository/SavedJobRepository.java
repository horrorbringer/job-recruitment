package com.recruitment.repository;

import com.recruitment.model.SavedJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {
    List<SavedJob> findByJobSeekerIdOrderByCreatedAtDesc(Long jobSeekerId);

    Optional<SavedJob> findByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);

    boolean existsByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);

    void deleteByJobSeekerIdAndJobId(Long jobSeekerId, Long jobId);
}
