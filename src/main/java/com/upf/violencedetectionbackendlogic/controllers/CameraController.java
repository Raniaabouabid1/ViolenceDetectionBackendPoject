package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.dtos.CameraDto;
import com.upf.violencedetectionbackendlogic.dao.entities.Camera;
import com.upf.violencedetectionbackendlogic.dao.entities.Section;
import com.upf.violencedetectionbackendlogic.dao.repositories.CameraRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.SectionRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/cameras")
public class CameraController {

    private final CameraRepository cameraRepository;
    private final SectionRepository sectionRepository;

    public CameraController(CameraRepository cameraRepository, SectionRepository sectionRepository) {
        this.cameraRepository = cameraRepository;
        this.sectionRepository = sectionRepository;
    }
    @PostMapping
    public ResponseEntity<Map<String, String>> registerCamera(@RequestBody Map<String, String> payload) {
        System.out.println("📥 Received camera registration request...");

        String token = payload.get("streamToken");
        String ip = payload.get("ipAddress");
        String streamUrl = payload.get("streamUrl");
        String name = payload.get("name");
        String sectionId = payload.get("sectionId");
        String flaskPort = payload.get("flaskPort");

        System.out.println("📦 Parsed payload:");
        System.out.println(" - Name: " + name);
        System.out.println(" - Stream Token: " + token);
        System.out.println(" - IP Address: " + ip);
        System.out.println(" - Stream URL: " + streamUrl);
        System.out.println(" - Section ID: " + sectionId);
        System.out.println(" - Port : " + flaskPort);

        if (token == null || ip == null || streamUrl == null || name == null) {
            System.out.println("❌ Missing one or more required fields!");
            return ResponseEntity.badRequest().body(Collections.singletonMap("message", "Missing required fields"));
        }

        Camera camera = cameraRepository.findByStreamToken(token).orElse(new Camera());
        camera.setStreamToken(token);
        camera.setLastKnownIp(ip);
        camera.setStreamUrl(streamUrl);
        camera.setName(name);
        camera.setIsActive(true);
        camera.setFlaskPort(Integer.parseInt(flaskPort));

        System.out.println("my flask port : "+ camera.getFlaskPort());

        if (sectionId != null) {
            sectionRepository.findById(UUID.fromString(sectionId)).ifPresent(camera::setSection);
        }

        cameraRepository.save(camera);
        System.out.println("✅ Camera saved/updated in DB with token: " + token);

        try {
            System.out.println("🚀 Attempting to launch Python stream script...");
            String pythonPath = "C:\\Users\\HP\\AppData\\Local\\Programs\\Python\\Python312\\python.exe";
            String port = payload.get("flaskPort"); // <-- new field
            Process p = Runtime.getRuntime().exec(
                    pythonPath + " D:\\stream_camera.py " + token + " " + ip + " " + port
            );


/*
            Process p = Runtime.getRuntime().exec(pythonPath + " D:\\stream_camera.py " + token + " " + ip);
*/
            Scanner errScanner = new Scanner(p.getErrorStream());
            while (errScanner.hasNextLine()) {
                System.err.println("🐍 PYTHON ERR >>> " + errScanner.nextLine());
            }
            errScanner.close();

        } catch (Exception e) {
            System.out.println("❌ Failed to trigger Python stream script:");
            e.printStackTrace();
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Camera registered or updated."));
    }



    @GetMapping
    public ResponseEntity<List<CameraDto>> getAllCameras() {
        List<Camera> cameras = cameraRepository.findAll();

        List<CameraDto> dtoList = cameras.stream().map(cam -> new CameraDto(
                cam.getId(),
                cam.getName(),
                cam.getSection() != null ? cam.getSection().getName() : null,
                cam.getSection() != null ? cam.getSection().getId().toString() : null,
                cam.getLastKnownIp(),
                cam.getStreamToken(),
                cam.getFlaskPort()
        )).toList();

        return ResponseEntity.ok(dtoList);
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

    /*@PostMapping
    public ResponseEntity<String> registerCamera(@RequestBody Map<String, String> payload) {
        String token = payload.get("streamToken");
        String ip = payload.get("ipAddress");
        String streamUrl = payload.get("streamUrl");
        String name = payload.get("name");

        Camera camera = cameraRepository.findByStreamToken(token).orElse(new Camera());
        camera.setStreamToken(token);
        camera.setLastKnownIp(ip);
        camera.setStreamUrl(streamUrl);
        camera.setName(name);
        camera.setIsActive(true);


        cameraRepository.save(camera);
        return ResponseEntity.ok("Camera registered or updated.");
    }
*/



    // ✅ Update Existing Camera
    @PutMapping("/{id}")
    public ResponseEntity<Camera> updateCamera(@PathVariable UUID id, @RequestBody Camera cameraData) {
        return cameraRepository.findById(id).map(existing -> {
            existing.setName(cameraData.getName());

            // Optional: assign new section
            if (cameraData.getSection() != null && cameraData.getSection().getId() != null) {
                Optional<Section> sectionOpt = sectionRepository.findById(cameraData.getSection().getId());
                sectionOpt.ifPresent(existing::setSection);
            } else {
                existing.setSection(null); // unassign section
            }

            Camera updated = cameraRepository.save(existing);
            return ResponseEntity.ok(updated);
        }).orElse(ResponseEntity.notFound().build());
    }
}

