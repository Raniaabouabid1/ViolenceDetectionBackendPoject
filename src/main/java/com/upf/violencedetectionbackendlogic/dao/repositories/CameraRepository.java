package com.upf.violencedetectionbackendlogic.dao.repositories;

import com.upf.violencedetectionbackendlogic.dao.entities.Camera;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CameraRepository extends JpaRepository<Camera, UUID> {
    List<Camera> findBySectionIsNull();
    List<Camera> findBySectionId(UUID sectionId);

}
