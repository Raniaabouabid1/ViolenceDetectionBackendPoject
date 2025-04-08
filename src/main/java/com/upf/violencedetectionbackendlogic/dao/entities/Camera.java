package com.upf.violencedetectionbackendlogic.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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

    @NotNull
    @Column(name = "name")
    private String name;

    @Column(name = "stream_url")
    private String streamUrl; //example ghaykoun : url = "http://10.1.0.155:4747/video"

    @Column(name = "stream_token")
    private String streamToken; // Unique identifier for registration (persistent)

    @Column(name = "last_known_ip")
    private String lastKnownIp; // Changes dynamically

    @Column(name = "flask_port")
    private int flaskPort;

    @Column(name = "is_active")
    private Boolean isActive; // Camera is online or not


    @OneToMany(mappedBy = "camera", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("camera-footage")
    private List<Footage> footages;

    @OneToMany(mappedBy = "senderCamera", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("camera-notification")
    private List<Notification> notifications;

    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    @JsonBackReference("camera-section")
    private Section section;

}
