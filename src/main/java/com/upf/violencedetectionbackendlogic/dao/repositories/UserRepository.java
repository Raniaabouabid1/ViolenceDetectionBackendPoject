package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.User; // Make sure this is your entity!
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);
}
