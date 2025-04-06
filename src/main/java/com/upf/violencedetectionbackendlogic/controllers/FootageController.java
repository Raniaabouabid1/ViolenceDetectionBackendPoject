package com.upf.violencedetectionbackendlogic.controllers;

import com.upf.violencedetectionbackendlogic.dao.entities.Camera;
import com.upf.violencedetectionbackendlogic.dao.entities.Footage;
import com.upf.violencedetectionbackendlogic.dao.repositories.CameraRepository;
import com.upf.violencedetectionbackendlogic.dao.repositories.FootageRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/footages")
public class FootageController {

    private final CameraRepository cameraRepository;
    private final FootageRepository footageRepository;

    public FootageController(CameraRepository cameraRepository, FootageRepository footageRepository) {
        this.cameraRepository = cameraRepository;
        this.footageRepository = footageRepository;
    }


    @GetMapping
    public void proxyStream(HttpServletResponse response) throws IOException {
        String droidCamUrl = "http://10.1.0.155:4747/video"; // DroidCam

        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Connection", "close");
        response.setContentType("multipart/x-mixed-replace; boundary=frame");

        InputStream inputStream = new URL(droidCamUrl).openStream();
        OutputStream outputStream = response.getOutputStream();

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(("--frame\r\n" +
                    "Content-Type: image/jpeg\r\n\r\n").getBytes());
            outputStream.write(buffer, 0, bytesRead);
            outputStream.write("\r\n".getBytes());
            outputStream.flush();
        }

        inputStream.close();
        outputStream.close();
    }

    @PostMapping
    public ResponseEntity<String> recordFootage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("cameraId") UUID cameraId
    ) throws IOException {
        Camera camera = cameraRepository.findById(cameraId).orElse(null);
        if (camera == null) {
            return ResponseEntity.badRequest().body("Camera not found");
        }

        String storagePath = "D:/"; // 👈 could be configurable
        String filename = UUID.randomUUID() + "_" + file.getOriginalFilename();
        Path filepath = Paths.get(storagePath, filename);
        Files.write(filepath, file.getBytes());

        Footage footage = new Footage();
        footage.setCamera(camera);
        footage.setTimestamp(LocalDateTime.now());
        footage.setFilePath(filepath.toString()); // ✅ Just save the path

        footageRepository.save(footage);
        return ResponseEntity.ok("Saved footage to: " + filepath.toString());
    }

}
