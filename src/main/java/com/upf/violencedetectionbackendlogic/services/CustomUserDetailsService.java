package com.upf.violencedetectionbackendlogic.services;

import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import com.upf.violencedetectionbackendlogic.security.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetailsService;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername called with email = " + email);
        Optional<User> optionalUser = userRepository.findByEmail(email);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new CustomUserDetails(optionalUser.get());
    }

    public UserDetails loadUserById(UUID id){
        System.out.println("loadUserById called with id = " + id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UsernameNotFoundException("User not found with id: " + id);
        }
        return new CustomUserDetails(optionalUser.get());
    }
}
