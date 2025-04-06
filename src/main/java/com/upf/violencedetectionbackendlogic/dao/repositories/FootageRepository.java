package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.Footage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FootageRepository extends JpaRepository<Footage, UUID> {
}
