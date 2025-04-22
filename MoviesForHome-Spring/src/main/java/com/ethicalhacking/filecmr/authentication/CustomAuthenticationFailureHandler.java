package com.ethicalhacking.filecmr.authentication;

import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.core.AuthenticationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String errorMessage;
        String exceptionMsg = exception.getMessage();

        if (exceptionMsg != null && exceptionMsg.toLowerCase().contains("user")) {
            errorMessage = "No account found with this username.";
        } else if (exceptionMsg != null && exceptionMsg.toLowerCase().contains("bad credentials")) {
            errorMessage = "Incorrect password. Please try again.";
        } else {
            errorMessage = "Login failed. Please try again.";
        }

        String encodedMessage = URLEncoder.encode(errorMessage, StandardCharsets.UTF_8);
        response.sendRedirect("/login?error=" + encodedMessage);
    }
}