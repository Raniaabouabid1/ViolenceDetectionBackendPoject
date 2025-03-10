package com.upf.violencedetectionbackendlogic.configuration;

import com.upf.violencedetectionbackendlogic.services.CustomUserDetailsService;
import com.upf.violencedetectionbackendlogic.utils.JwtUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@Configuration
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtRequestFilter jwtRequestFilter;
    private final JwtUtil jwtUtil;

    public SecurityConfig(CustomUserDetailsService userDetailsService, JwtRequestFilter jwtRequestFilter, JwtUtil jwtUtil) {
        this.userDetailsService = userDetailsService;
        this.jwtRequestFilter = jwtRequestFilter;
        this.jwtUtil = jwtUtil;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        // Create your custom JWT authentication filter with the authManager and JWT utility.
        JwtAuthenticationFilter jwtAuthFilter = new JwtAuthenticationFilter(authManager, jwtUtil);

        http
                // Disable CSRF using the new lambda DSL style.
                .csrf(AbstractHttpConfigurer::disable)
                // Define authorization rules.
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login").permitAll()
                        .anyRequest().authenticated()
                )
                // Set the session management to stateless.
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // Add the custom login filter.
        http.addFilter(jwtAuthFilter);
        // Add the JWT request filter before UsernamePasswordAuthenticationFilter.
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
