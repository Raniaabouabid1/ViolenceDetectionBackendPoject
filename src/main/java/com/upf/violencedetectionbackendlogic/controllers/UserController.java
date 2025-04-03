package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.UserDto;
import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.RoleRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import com.upf.violencedetectionbackendlogic.dao.dtos.ProfileDto;
import com.upf.violencedetectionbackendlogic.dao.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<User>> getUnassignedUsers() {
        List<User> users = userRepository.findUsersWithoutSection();
        return ResponseEntity.ok(users);
    }
    @GetMapping("/for-section/{sectionId}")
    public ResponseEntity<List<User>> getAssignedAndUnassignedUsers(@PathVariable UUID sectionId) {
        List<User> users = userRepository.findUsersForSection(sectionId);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/assigned-to-section/{sectionId}")
    public ResponseEntity<List<User>> getAssignedUsers(@PathVariable UUID sectionId) {
        List<User> users = userRepository.findBySectionId(sectionId);
        return ResponseEntity.ok(users);
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

        String encodedPassword = passwordEncoder.encode(createdUserDto.getPassword());
        user.setPassword(encodedPassword);

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
        System.out.println("this is my id : " + id);
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

        if (user.getSection() != null) {
            dto.setSectionName(user.getSection().getName());
        } else {
            dto.setSectionName("");
        }

        if (user.getRole() != null) {
            dto.setRoleName(user.getRole().getRole().name());
        }

        System.out.println("📍 Section name returned in DTO: " + dto.getSectionName());
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
    public ResponseEntity<Page<UserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size,
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String searchEmail,
            @RequestParam(required = false) String role) {

        Pageable pageable = PageRequest.of(page, size);
        Page<User> userPage = userRepository.findAll(pageable);

        List<User> filteredUsers = userPage.getContent();

        if (fullName != null && !fullName.isEmpty()) {
            String lower = fullName.toLowerCase();
            filteredUsers = filteredUsers.stream().filter(user -> (user.getFirstName() + " " + user.getLastName()).toLowerCase().contains(lower)).toList();
        }

        if (searchEmail != null && !searchEmail.isEmpty()) {
            filteredUsers = filteredUsers.stream().filter(user -> user.getEmail().toLowerCase().contains(searchEmail.toLowerCase())).toList();
        }

        if (role != null && !role.isEmpty()) {
            filteredUsers = filteredUsers.stream().filter(user -> user.getRole() != null &&
                    user.getRole().getRole().name().equalsIgnoreCase(role)).toList();
        }

        List<UserDto> dtos = filteredUsers.stream().map(user -> {
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
        }).toList();

        Page<UserDto> dtoPage = new PageImpl<>(dtos, pageable, userPage.getTotalElements());

        return ResponseEntity.ok(dtoPage);
    }


    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID id, @RequestBody UserDto updatedUserDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();
        user.setFirstName(updatedUserDto.getFirstName());
        user.setLastName(updatedUserDto.getLastName());
        user.setBirthDate(updatedUserDto.getBirthDate());
        user.setEmail(updatedUserDto.getEmail());
        user.setPhoneNumber(updatedUserDto.getPhoneNumber());

        String encodedPassword = passwordEncoder.encode(updatedUserDto  .getPassword());
        user.setPassword(encodedPassword);

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
        return ResponseEntity.ok(dto);
    }
}
