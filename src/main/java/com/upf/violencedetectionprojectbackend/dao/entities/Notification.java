package com.upf.violencedetectionprojectbackend.dao.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name="notification")
public class Notification {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    private String message;

    private LocalDateTime timestamp;

    private Boolean violenceRate;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User recipientUser;

    @ManyToOne
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera senderCamera;



}
