package com.upf.violencedetectionprojectbackend.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "camera")
public class Camera {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Footage> footages;

    @OneToMany(mappedBy = "senderCamera", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    private Section section;
}
