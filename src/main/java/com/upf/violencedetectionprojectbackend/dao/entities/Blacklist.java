package com.upf.violencedetectionprojectbackend.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.upf.violencedetectionprojectbackend.dao.entities.enumerations.ViolenceType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "blacklist")
public class Blacklist {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "violenceType", nullable = false)
    private List<ViolenceType> violenceTypes;

    @NotNull
    @Column(name = "violenceRate", nullable = false)
    private Boolean violenceRate;

    @NotBlank
    @Column(name = "imageCaptureServerLocation", nullable = false)
    private String imageCaptureServerLocation;

    @NotNull
    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp;

    @ManyToOne
    @JoinColumn(name = "footage_id", nullable = false)
    @JsonBackReference
    private Footage footage;
}
