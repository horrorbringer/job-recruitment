package com.recruitment.repository;

import com.recruitment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByEmail(String email);

        boolean existsByEmail(String email);

        long countByRole(User.Role role);

        long countByCreatedAtAfter(java.time.LocalDateTime date);

        @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE " +
                        "(:role IS NULL OR u.role = :role) AND " +
                        "(:enabled IS NULL OR u.enabled = :enabled) AND " +
                        "(:locked IS NULL OR u.accountNonLocked != :locked)")
        org.springframework.data.domain.Page<User> findFilteredUsers(
                        @org.springframework.data.repository.query.Param("role") User.Role role,
                        @org.springframework.data.repository.query.Param("enabled") Boolean enabled,
                        @org.springframework.data.repository.query.Param("locked") Boolean locked,
                        org.springframework.data.domain.Pageable pageable);
}
