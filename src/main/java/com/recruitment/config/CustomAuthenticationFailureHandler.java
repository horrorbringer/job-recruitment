package com.recruitment.config;

import com.recruitment.model.User;
import com.recruitment.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final int LOCK_MINUTES = 30;

    private final UserRepository userRepository;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, 
                                        HttpServletResponse response, 
                                        AuthenticationException exception) throws IOException, ServletException {
        
        String email = request.getParameter("username");
        String errorMessage;
        
        try {
            if (exception instanceof BadCredentialsException) {
                User user = null;
                try {
                    user = userRepository.findByEmail(email).orElse(null);
                } catch (Exception e) {
                    // Database error - just show generic message
                }
                
                if (user != null && user.isEnabled()) {
                    user.incrementFailedAttempts();
                    
                    if (user.getFailedLoginAttempts() >= MAX_FAILED_ATTEMPTS) {
                        user.lockAccount(LOCK_MINUTES);
                        userRepository.save(user);
                        errorMessage = "Too many failed login attempts. Account locked for " + LOCK_MINUTES + " minutes.";
                    } else {
                        int remaining = MAX_FAILED_ATTEMPTS - user.getFailedLoginAttempts();
                        userRepository.save(user);
                        errorMessage = "Invalid email or password. " + remaining + " attempts remaining before account lock.";
                    }
                } else {
                    errorMessage = "Invalid email or password";
                }
            } else if (exception instanceof DisabledException) {
                errorMessage = "Your account has been disabled. Please contact admin.";
            } else if (exception instanceof LockedException) {
                errorMessage = "Your account has been locked. Please contact admin.";
            } else {
                errorMessage = "Login failed. Please try again.";
            }
        } catch (Exception e) {
            errorMessage = "An error occurred. Please try again.";
        }
        
        request.getSession().setAttribute("loginError", errorMessage);
        response.sendRedirect("/login?error=true");
    }
}
