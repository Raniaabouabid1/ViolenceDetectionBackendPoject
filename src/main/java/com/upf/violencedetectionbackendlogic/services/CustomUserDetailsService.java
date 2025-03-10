package com.upf.violencedetectionbackendlogic.services;

import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        User userEntity = optionalUser.get();
        // Return a Spring Security User (fully qualify the class to avoid ambiguity)
        return org.springframework.security.core.userdetails.User
                .withUsername(userEntity.getEmail())
                .password(userEntity.getPassword())
                .authorities("ROLE_SECURITY_AGENT")
                .build();
    }

    // Helper method to create/save new users
    public User saveUser(String email, String rawPassword) {
        User user = new User();
        user.setEmail(email);
        user.setPassword(encoder.encode(rawPassword));
        return userRepository.save(user);
    }
}
