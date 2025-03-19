package com.upf.violencedetectionbackendlogic.dao.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
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
    @Column(name = "first_name", nullable = false)
    private String firstName;

    @NotNull
    @NotBlank
    @Pattern(regexp = "^[a-zA-Z]+([ -][a-zA-Z]+)*$")
    @Column(name = "last_name", nullable = false)
    private String lastName;

    @NotNull
    @NotBlank
    @Email
    @Column(name="email", nullable=false, unique=true)
    private String email;

    @NotBlank
    @Pattern(regexp = "\\+212[6-7][0-9]{8}", message = "Must be a valid Moroccan phone number")
    @Column(name = "phone_number", nullable = false, unique = true)
    protected String PhoneNumber;

    @NotNull
    @Column(name = "password", unique = true, nullable = false)/*
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must be at least 8 characters long, contain an uppercase letter, a lowercase letter, a number, and a special character")*/
    private String password;

    @NotNull
    @Column(name = "birth_date", unique = true, nullable = false)
    private LocalDate birthDate;

    @Lob
    @Column(name = "profile_image", length = 5_000_000)
    private byte[] profileImage;


    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    @JsonBackReference
    protected Role role;

    // Each user belongs to one section; make sure the foreign key is nullable.
    @ManyToOne
    @JoinColumn(name = "section_id", nullable = true)
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.SET_NULL) // Optional: auto set null on section deletion (Hibernate only)
    private Section section;

    @OneToMany(mappedBy = "recipientUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications;

    public String getRoleName(String roleName) {
        return this.getRole().toString();
    }


}
