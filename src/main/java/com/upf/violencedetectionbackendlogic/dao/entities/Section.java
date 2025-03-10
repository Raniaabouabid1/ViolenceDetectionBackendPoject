package com.upf.violencedetectionbackendlogic.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "coordinates")
    private String Coordinates;

    // One section can have many users; we do not cascade REMOVE so that users are not deleted.
    @OneToMany(mappedBy = "section", cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JsonManagedReference
    private List<User> users;

    @OneToMany(mappedBy = "section", cascade = CascadeType.PERSIST, orphanRemoval = false)
    private List<Camera> cameras;
}
