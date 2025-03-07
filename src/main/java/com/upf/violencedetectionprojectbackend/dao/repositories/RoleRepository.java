package com.upf.violencedetectionprojectbackend.dao.repositories;

import com.upf.violencedetectionprojectbackend.dao.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface RoleRepository extends JpaRepository<Role, UUID> {
}