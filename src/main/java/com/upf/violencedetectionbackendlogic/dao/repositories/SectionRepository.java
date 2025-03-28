package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.Section;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface SectionRepository extends JpaRepository<Section, UUID> {
    Page<Section> findByNameContainingIgnoreCaseAndCoordinatesContainingIgnoreCase(String name, String coordinates, Pageable pageable);

}
