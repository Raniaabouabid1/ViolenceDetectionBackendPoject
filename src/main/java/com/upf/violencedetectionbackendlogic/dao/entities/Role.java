package com.upf.violencedetectionbackendlogic.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.upf.violencedetectionbackendlogic.dao.entities.enumerations.RoleEnum;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", unique = true, nullable = false)
    private RoleEnum role;

    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<User> users = new ArrayList<>();
}
