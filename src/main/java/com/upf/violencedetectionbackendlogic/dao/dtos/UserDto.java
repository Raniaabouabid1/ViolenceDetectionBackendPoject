package com.upf.violencedetectionbackendlogic.dao.dtos;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class UserDto {
    private String firstName;
    private String lastName;
    private String email;
    private UUID id;
    private String phoneNumber;
    private LocalDate birthDate;
    private String roleName;
    private String assignedSection;
    private String password;

    public UserDto() {}

    public UserDto(String firstName, String lastName, String email, String phoneNumber, LocalDate birthDate) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
    }

    public UserDto(String firstName, String lastName, String email, UUID id, String phoneNumber, LocalDate birthDate, String roleName, String assignedSection, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.birthDate = birthDate;
        this.roleName = roleName;
        this.assignedSection = assignedSection;
        this.password = password;

    }
}

