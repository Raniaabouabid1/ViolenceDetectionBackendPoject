package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(String email);

    Optional<User> findByPhoneNumber(String phoneNumber);

    @Query("SELECT u FROM User u WHERE u.section IS NULL")
    List<User> findUsersWithoutSection();

    @Query("SELECT u FROM User u WHERE u.section IS NULL OR u.section.id = :sectionId")
    List<User> findUsersForSection(@Param("sectionId") UUID sectionId);

    List<User> findBySectionId(UUID sectionId);


}
