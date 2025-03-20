package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.UserDto;
import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.RoleRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import com.upf.violencedetectionbackendlogic.dao.dtos.ProfileDto;
import com.upf.violencedetectionbackendlogic.dao.entities.Role;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.upf.violencedetectionbackendlogic.dao.entities.enumerations.RoleEnum;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    public UserController(UserRepository userRepository, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto createdUserDto) {
        System.out.println("adding a user: " + createdUserDto);

        User user = new User();
        user.setFirstName(createdUserDto.getFirstName());
        user.setLastName(createdUserDto.getLastName());
        user.setBirthDate(createdUserDto.getBirthDate());
        user.setEmail(createdUserDto.getEmail());
        user.setPhoneNumber(createdUserDto.getPhoneNumber());
        user.setPassword(createdUserDto.getPassword());

        // Convert the incoming role string to RoleEnum
        RoleEnum roleEnum;
        try {
            roleEnum = RoleEnum.valueOf(createdUserDto.getRole().toUpperCase());
        } catch (IllegalArgumentException e) {
            // If conversion fails, return a bad request
            return ResponseEntity.badRequest().build();
        }

        // Fetch the Role entity using the RoleEnum
        Role role = roleRepository.findByRole(roleEnum);
        if (role == null) {
            return ResponseEntity.badRequest().build();
        }
        user.setRole(role);

        User savedUser = userRepository.save(user);

        UserDto dto = new UserDto();
        dto.setId(savedUser.getId());
        dto.setFirstName(savedUser.getFirstName());
        dto.setLastName(savedUser.getLastName());
        dto.setEmail(savedUser.getEmail());
        dto.setPhoneNumber(savedUser.getPhoneNumber());
        dto.setBirthDate(savedUser.getBirthDate());
        if (savedUser.getRole() != null) {
            dto.setRole(savedUser.getRole().getRole().name());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getUser(@PathVariable UUID id) {
        System.out.println("this is my id : "+id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();

        ProfileDto dto = new ProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setBirthDate(user.getBirthDate());
        if(dto.getSectionName() != null){
            dto.setSectionName(user.getSection().getName());
        }else {
            dto.setSectionName("");
        }


        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRole().name());
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        System.out.println("this is the id of the wanted deleting user: "+id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        User user = optionalUser.get();
        userRepository.delete(user);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String searchEmail,
            @RequestParam(required = false) String role) {
            System.out.println("this is my search email : "+searchEmail);
            System.out.println("this is my search fullname : "+fullName);
            System.out.println("this is my role : "+role);

        List<User> users = userRepository.findAll();

        // Filter by full name if provided.
        if (fullName != null && !fullName.isEmpty()) {
            String lowerFullName = fullName.toLowerCase();
            users = users.stream().filter(user -> {
                String combinedName = (user.getFirstName() + " " + user.getLastName()).toLowerCase();
                return combinedName.contains(lowerFullName);
            }).collect(Collectors.toList());
        }

        // Filter by email if provided.
        if (searchEmail != null && !searchEmail.isEmpty()) {
            String lowerEmail = searchEmail.toLowerCase();
            users = users.stream().filter(user -> user.getEmail().toLowerCase().contains(lowerEmail))
                    .collect(Collectors.toList());
        }
        if (role != null && !role.isEmpty()) {
            users = users.stream().filter(user -> user.getRole() != null &&
                            user.getRole().getRole().name().equalsIgnoreCase(role))
                    .collect(Collectors.toList());
        }

        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setBirthDate(user.getBirthDate());
            if (user.getRole() != null) {
                dto.setRole(user.getRole().getRole().name());
            }
            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody UserDto updatedUserDto) {
        // Look up the existing user by id
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        // Update user fields from DTO
        user.setFirstName(updatedUserDto.getFirstName());
        user.setLastName(updatedUserDto.getLastName());
        user.setBirthDate(updatedUserDto.getBirthDate());
        user.setEmail(updatedUserDto.getEmail());
        user.setPhoneNumber(updatedUserDto.getPhoneNumber());
        // If you want to allow updating the password, you can do so here (with proper security checks)
        user.setPassword(updatedUserDto.getPassword());

        // Update the role if provided
        String roleString = updatedUserDto.getRole();
        if (roleString != null) {
            try {
                RoleEnum roleEnum = RoleEnum.valueOf(roleString.toUpperCase());
                Role role = roleRepository.findByRole(roleEnum);
                if (role == null) {
                    return ResponseEntity.badRequest().build();
                }
                user.setRole(role);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        // Save updated user
        User savedUser = userRepository.save(user);

        // Map saved user to UserDto and return
        UserDto dto = new UserDto();
        dto.setId(savedUser.getId());
        dto.setFirstName(savedUser.getFirstName());
        dto.setLastName(savedUser.getLastName());
        dto.setEmail(savedUser.getEmail());
        dto.setPhoneNumber(savedUser.getPhoneNumber());
        dto.setBirthDate(savedUser.getBirthDate());
        if (savedUser.getRole() != null) {
            dto.setRole(savedUser.getRole().getRole().name());
        }
        return ResponseEntity.ok(dto);
    }

}
