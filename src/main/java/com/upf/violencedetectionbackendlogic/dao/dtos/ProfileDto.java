package com.upf.violencedetectionbackendlogic.dao.dtos;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;

@Getter
@Setter
public class ProfileDto {
    private String firstName;
    private String lastName;
    private String email;
    private String telephone;
    private LocalDate birthDate;
    private String roleName;
    private String sectionName;

}
