package com.recruitment.config;

import com.recruitment.service.UserService;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.*;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        String email = authentication.getName();
        userService.onLoginSuccess(email);

        String redirectUrl = "/";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            if (auth.getAuthority().equals("ROLE_ADMIN")) {
                redirectUrl = "/admin/dashboard";
                break;
            } else if (auth.getAuthority().equals("ROLE_RECRUITER")) {
                redirectUrl = "/recruiter/dashboard";
                break;
            } else if (auth.getAuthority().equals("ROLE_JOB_SEEKER")) {
                redirectUrl = "/job-seeker/dashboard";
                break;
            }
        }

        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}
