package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.SectionDTO;
import com.upf.violencedetectionbackendlogic.dao.entities.Camera;
import com.upf.violencedetectionbackendlogic.dao.entities.Section;
import com.upf.violencedetectionbackendlogic.dao.entities.User;
import com.upf.violencedetectionbackendlogic.dao.repositories.CameraRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.SectionRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/sections")
public class SectionController {

    private final SectionRepository sectionRepository;
    private final UserRepository userRepository;
    private final CameraRepository cameraRepository;

    public SectionController(SectionRepository sectionRepository, UserRepository userRepository, CameraRepository cameraRepository) {
        this.sectionRepository = sectionRepository;
        this.userRepository = userRepository;
        this.cameraRepository = cameraRepository;
    }

    @GetMapping
    public ResponseEntity<Page<Section>> getSections(
            @RequestParam(defaultValue = "") String name,
            @RequestParam(defaultValue = "") String coordinates,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Section> result = sectionRepository.findByNameContainingIgnoreCaseAndCoordinatesContainingIgnoreCase(name, coordinates, pageable);
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Section> addSection(@RequestBody SectionDTO sectionDTO) {
        System.out.println("the section im trying to add " + sectionDTO);

        Section section = new Section();
        section.setName(sectionDTO.name);
        section.setCoordinates(sectionDTO.coordinates);

        List<User> users = userRepository.findAllById(sectionDTO.userIds);


        for (User user : users) {
            if (user.getSection() != null) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body(null);
            }
        }

        for (User user : users) {
            user.setSection(section);
        }


        List<Camera> cameras = cameraRepository.findAllById(sectionDTO.cameraIds);

        for (Camera camera : cameras) {
            camera.setSection(section);
        }
        section.setCameras(cameras);

        Section savedSection = sectionRepository.save(section);

        userRepository.saveAll(users);

        System.out.println(savedSection.getName());
        System.out.println(savedSection.getId());
        System.out.println(savedSection.getUsers());
        System.out.println(savedSection.getCameras());

        return ResponseEntity.ok(savedSection);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteSection(@PathVariable UUID id) {
        return sectionRepository.findById(id).map(section -> {
            section.getUsers().forEach(user -> user.setSection(null));
            userRepository.saveAll(section.getUsers());

            section.getCameras().forEach(camera -> camera.setSection(null));
            cameraRepository.saveAll(section.getCameras());

            sectionRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }).orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Section> updateSection(@PathVariable UUID id, @RequestBody SectionDTO sectionDTO) {
        return sectionRepository.findById(id).map(section -> {
            // ✅ Update basic fields
            section.setName(sectionDTO.name);
            section.setCoordinates(sectionDTO.coordinates);

            // ✅ Clear previous user assignments
            section.getUsers().forEach(user -> user.setSection(null));
            userRepository.saveAll(section.getUsers());

            // ✅ Assign new users (if any)
            List<User> newUsers = userRepository.findAllById(sectionDTO.userIds);
            for (User user : newUsers) {
                user.setSection(section);
            }

            // ✅ Clear previous camera assignments
            section.getCameras().forEach(camera -> camera.setSection(null));
            cameraRepository.saveAll(section.getCameras());

            // ✅ Assign new cameras
            List<Camera> newCameras = cameraRepository.findAllById(sectionDTO.cameraIds);
            for (Camera camera : newCameras) {
                camera.setSection(section);
            }

            section.setUsers(newUsers);
            section.setCameras(newCameras);

            Section updated = sectionRepository.save(section);
            userRepository.saveAll(newUsers);
            cameraRepository.saveAll(newCameras);

            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }


}
