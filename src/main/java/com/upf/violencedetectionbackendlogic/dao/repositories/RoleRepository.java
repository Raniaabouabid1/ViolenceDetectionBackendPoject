package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.Role;
import com.upf.violencedetectionbackendlogic.dao.entities.enumerations.RoleEnum;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(@NotNull RoleEnum role);

}
