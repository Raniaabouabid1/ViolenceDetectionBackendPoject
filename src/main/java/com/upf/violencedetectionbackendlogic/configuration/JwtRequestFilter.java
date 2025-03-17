package com.upf.violencedetectionbackendlogic.configuration;

import com.upf.violencedetectionbackendlogic.services.CustomUserDetailsService;
import com.upf.violencedetectionbackendlogic.utils.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    private final CustomUserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Autowired
    public JwtRequestFilter(CustomUserDetailsService userDetailsService, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtUtil = jwtUtil;
    }

    // Skip filtering for login endpoint
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getServletPath();
        // Skip filtering for /login and /error endpoints
        if (path.equals("/login") || path.equals("/error")) {
            System.out.println("Skipping JWT check for " + path);
            return true;
        }
        return false;
    }



        @Override
        protected void doFilterInternal(HttpServletRequest request,
                                        HttpServletResponse response,
                                        FilterChain filterChain)
                throws IOException, ServletException {

            final String authHeader = request.getHeader("Authorization");
            String userIdString = null;
            String token = null;

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                token = authHeader.substring(7);
                userIdString = jwtUtil.extractUserId(token); // extract the id here
            }

            if (userIdString != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                try {

                    UUID userId = UUID.fromString(userIdString);
                    UserDetails userDetails = userDetailsService.loadUserById(userId);
                    if (jwtUtil.validateToken(token, userDetails)) {
                        UsernamePasswordAuthenticationToken authToken =
                                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        SecurityContextHolder.getContext().setAuthentication(authToken);
                    }
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                }
            }
            filterChain.doFilter(request, response);
        }
    }


