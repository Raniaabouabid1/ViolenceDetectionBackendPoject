package com.upf.violencedetectionprojectbackend.dao.entities;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
@Table(name = "user")
public class User {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+([ -][a-zA-Z]+)*$")
    @Column(name = "firstName", nullable = false)
    private String firstName;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+([ -][a-zA-Z]+)*$")
    @Column(name = "lastName", nullable = false)
    private String lastName;

    @NotNull
    @NotBlank
    @Email
    @Column(name="email", nullable=false, unique=true)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\+212[6-7][0-9]{8}", message = "Must be a valid Moroccan phone number")
    @Column(name = "phoneNumber", nullable = false, unique = true)
    protected String telephone;

    @NotNull
    @Column(name = "password", unique = true, nullable = false)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
    message = "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character")
    private String password;

    @NotNull
    @Column(name = "birthDate", unique = true, nullable = false)
    private LocalDate birthDate;

    @Lob
    @Column(name = "profileImage", length = 5_000_000)
    private byte[] profileImage;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonManagedReference
    protected Role role;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    @JsonManagedReference
    private Section assignedSections;

    @OneToMany(mappedBy = "recipientUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;


}
