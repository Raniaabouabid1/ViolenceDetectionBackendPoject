package com.upf.violencedetectionprojectbackend.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name="section")
public class Section {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @NotNull
    @Column(name = "coordinates", nullable = false)
    private String Coordinates;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private List<User> user;

    @OneToMany(mappedBy = "section", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Camera> cameras;
}
