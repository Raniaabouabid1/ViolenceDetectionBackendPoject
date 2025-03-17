package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.UserDto;
import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import com.upf.violencedetectionbackendlogic.dao.dtos.ProfileDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfileDto> getUser(@PathVariable UUID id) {
        System.out.println("this is my id : "+id);
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        User user = optionalUser.get();

        // Build the DTO
        ProfileDto dto = new ProfileDto();
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setTelephone(user.getPhoneNumber());
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
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<User> users = userRepository.findAll();

        List<UserDto> userDtos = users.stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setBirthDate(user.getBirthDate());

            // If user.getRole() is non-null, flatten the role name:
            if (user.getRole() != null) {
                dto.setRoleName(user.getRole().getRole().name());
            }

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(userDtos);
    }

}
