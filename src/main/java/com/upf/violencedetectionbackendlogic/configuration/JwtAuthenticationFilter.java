package com.upf.violencedetectionbackendlogic.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upf.violencedetectionbackendlogic.dao.dtos.LoginRequest;
import com.upf.violencedetectionbackendlogic.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import com.upf.violencedetectionbackendlogic.security.CustomUserDetails;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public JwtAuthenticationFilter(AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login"); // This must match your login URL.
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter: attemptAuthentication called");
        HttpServletRequest wrappedRequest = request instanceof ContentCachingRequestWrapper
                ? request : new ContentCachingRequestWrapper(request);
        try {
            String body = new BufferedReader(new InputStreamReader(wrappedRequest.getInputStream()))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Full request body: " + body);
            if (body.isEmpty()) {
                throw new AuthenticationServiceException("No login data provided in the request body.");
            }
            LoginRequest creds = new ObjectMapper().readValue(body, LoginRequest.class);
            System.out.println("Received credentials: " + creds);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword());
            System.out.println("Authentication attempt token: " + authToken);
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read login request", e);
        }
    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {
        System.out.println("JwtAuthenticationFilter: successfulAuthentication called");
        // Assuming authResult.getPrincipal() returns an instance of CustomUserDetails:
        CustomUserDetails customUserDetails = (CustomUserDetails) authResult.getPrincipal();
        String userId = customUserDetails.getId().toString();
        System.out.println("Successfully authenticated user id: " + userId);
        String token = jwtUtil.generateToken(userId);
        response.addHeader("Authorization", "Bearer " + token);
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }

}
