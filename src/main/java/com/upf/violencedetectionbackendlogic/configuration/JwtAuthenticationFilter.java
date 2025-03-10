package com.upf.violencedetectionbackendlogic.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.upf.violencedetectionbackendlogic.dao.dtos.LoginRequest;
import com.upf.violencedetectionbackendlogic.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.*;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

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
        // Override default login endpoint
        setFilterProcessesUrl("/login");
    }
// other imports ...

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {
        // Wrap the request if not already wrapped
        HttpServletRequest wrappedRequest = request;
        if (!(request instanceof ContentCachingRequestWrapper)) {
            wrappedRequest = new ContentCachingRequestWrapper(request);
        }
        try {
            // Read the full body for debugging
            String body = new BufferedReader(new InputStreamReader(wrappedRequest.getInputStream()))
                    .lines()
                    .collect(Collectors.joining(System.lineSeparator()));
            System.out.println("Full request body: " + body);

            if (body.isEmpty()) {
                throw new AuthenticationServiceException("No login data provided in the request body.");
            }

            LoginRequest creds = new ObjectMapper().readValue(body, LoginRequest.class);
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(creds.getEmail(), creds.getPassword());
            return authenticationManager.authenticate(authToken);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read login request", e);
        }
    }



    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response,
                                            FilterChain chain, Authentication authResult)
            throws IOException {
        System.out.println("Successfully authenticated user: " + authResult.getPrincipal());
        String username = authResult.getName(); // email
        String token = jwtUtil.generateToken(username);

        // Return the token in the Authorization header (or JSON body)
        response.addHeader("Authorization", "Bearer " + token);

        // Optional: Write the token as JSON
        response.setContentType("application/json");
        response.getWriter().write("{\"token\": \"" + token + "\"}");
    }
}
