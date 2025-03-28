package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.UserDto;
import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class ProfileController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ProfileController(final UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@PathVariable UUID id, @RequestBody UserDto updateDto) {
       System.out.println("update dto "+updateDto);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Update fields if they are provided in the DTO
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }


        if (updateDto.getBirthDate() != null) {
            user.setBirthDate(updateDto.getBirthDate());
        }
        if (updateDto.getPassword() != null && !updateDto.getPassword().isBlank()) {

            String encodedPassword = passwordEncoder.encode(updateDto.getPassword());
            user.setPassword(encodedPassword);
        }

        if (updateDto.getEmail() != null) {
            // Only check if the new email is different from the current one
            if (!updateDto.getEmail().equalsIgnoreCase(user.getEmail())) {
                Optional<User> existingUser = userRepository.findByEmail(updateDto.getEmail());
                if (existingUser.isPresent()) {
                    System.out.println("already exists!!!!!!!!!");
                    // Return a 409 Conflict response with a message
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("email alreay exists");
                }
                user.setEmail(updateDto.getEmail());
            }
        }
        if (updateDto.getPhoneNumber() != null) {
            // Only check if the new email is different from the current one
            if (!updateDto.getPhoneNumber().equalsIgnoreCase(user.getPhoneNumber())) {
                Optional<User> existingUser = userRepository.findByPhoneNumber(updateDto.getPhoneNumber());
                if (existingUser.isPresent()) {
                    System.out.println("already exists!!!!!!!!!");
                    // Return a 409 Conflict response with a message
                    return ResponseEntity.status(HttpStatus.CONFLICT).body("phone number already exists");
                }
                user.setPhoneNumber(updateDto.getPhoneNumber());
            }
        }
        User updatedUser = userRepository.save(user);
        System.out.println("updated user "+updatedUser);
        return ResponseEntity.ok(updatedUser);
    }


    // Upload or update profile image
    @PutMapping("/{userId}/profile-image")
    public ResponseEntity<String> uploadProfileImage(
            @PathVariable UUID userId,
            @RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("File is empty");
        }

        try {
            Optional<User> userOptional = userRepository.findById(userId);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                user.setProfileImage(file.getBytes());
                userRepository.save(user);

                return ResponseEntity.ok("Profile image updated successfully");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing file: " + e.getMessage());
        }
    }

    // Get profile image
    @GetMapping("/{userId}/profile-image")
    public ResponseEntity<byte[]> getProfileImage(@PathVariable UUID userId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent() && userOptional.get().getProfileImage() != null) {
            return ResponseEntity.ok()
                    .header("Content-Type", "image/jpeg")
                    .body(userOptional.get().getProfileImage());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

}
