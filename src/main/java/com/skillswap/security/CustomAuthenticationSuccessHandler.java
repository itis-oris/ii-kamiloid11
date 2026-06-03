package com.skillswap.security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    private final LoginRateLimiter rateLimiter;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        String username = authentication.getName();
        rateLimiter.resetAttempts(username);

        String targetUrl = request.getParameter("redirect");
        if (targetUrl != null && !targetUrl.isBlank() && targetUrl.startsWith("/") && !targetUrl.startsWith("//")) {
            getRedirectStrategy().sendRedirect(request, response, targetUrl);
            return;
        }

        setDefaultTargetUrl("/offers");
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
