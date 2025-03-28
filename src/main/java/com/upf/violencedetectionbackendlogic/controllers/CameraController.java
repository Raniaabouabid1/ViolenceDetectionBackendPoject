package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.entities.Camera;
import com.upf.violencedetectionbackendlogic.dao.repositories.CameraRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraRepository cameraRepository;

    public CameraController(CameraRepository cameraRepository) {
        this.cameraRepository = cameraRepository;
    }

    @GetMapping
    public ResponseEntity<List<Camera>> getAllCameras() {
        List<Camera> cameras = cameraRepository.findAll();
        return ResponseEntity.ok(cameras);
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<Camera>> getUnassignedCameras() {
        List<Camera> cameras = cameraRepository.findBySectionIsNull();
        return ResponseEntity.ok(cameras);
    }

    @GetMapping("/assigned-to-section/{sectionId}")
    public ResponseEntity<List<Camera>> getCamerasAssignedToSection(@PathVariable UUID sectionId) {
        List<Camera> cameras = cameraRepository.findBySectionId(sectionId);
        return ResponseEntity.ok(cameras);
    }

    @GetMapping("/for-section/{sectionId}")
    public ResponseEntity<List<Camera>> getCamerasForEdit(@PathVariable UUID sectionId) {
        List<Camera> unassigned = cameraRepository.findBySectionIsNull();
        List<Camera> assigned = cameraRepository.findBySectionId(sectionId);

        Set<Camera> all = new HashSet<>();
        all.addAll(unassigned);
        all.addAll(assigned);

        return ResponseEntity.ok(new ArrayList<>(all));
    }
}

