package com.upf.violencedetectionbackendlogic.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table(name = "footage")
public class Footage {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @Column(name = "video_length", nullable = false)
    private double videoLength;

    @NotNull
    @Column(name = "day", nullable = false)
    private LocalDate day;

    @NotNull
    @Column(name = "footage_server_location", nullable = false)
    private String footageServerLocation;

    @ManyToOne
    @JoinColumn(name = "camera_id", nullable = false)
    private Camera camera;

    @OneToMany(mappedBy = "footage", fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Blacklist> blacklists;
}
