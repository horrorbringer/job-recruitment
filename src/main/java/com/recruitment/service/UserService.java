package com.recruitment.service;

import com.recruitment.model.User;
import com.recruitment.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));

        if (user.getLockedUntil() != null && user.getLockedUntil().isBefore(java.time.LocalDateTime.now())) {
            user.unlockAccount();
            userRepository.save(user);
        }

        return org.springframework.security.core.userdetails.User
            .withUsername(user.getEmail())
            .password(user.getPassword())
            .roles(user.getRole().name())
            .disabled(!user.isEnabled())
            .accountLocked(user.isAccountLocked())
            .build();
    }

    @Transactional
    public void onLoginSuccess(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.resetFailedAttempts();
            userRepository.save(user);
        });
    }

    @Transactional
    public User createUser(String email, String password, User.Role role) {
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already registered");
        }
        
        User user = new User(email, passwordEncoder.encode(password), role);
        user.setEnabled(true);
        return userRepository.save(user);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Transactional
    public void enableUser(String email) {
        User user = findByEmail(email);
        if (user != null) {
            user.setEnabled(true);
            userRepository.save(user);
        }
    }

    @Transactional
    public void lockUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.lockAccount(0);
            userRepository.save(user);
        });
    }

    @Transactional
    public void unlockUser(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.unlockAccount();
            userRepository.save(user);
        });
    }

    public long countByRole(User.Role role) {
        return userRepository.countByRole(role);
    }
}
