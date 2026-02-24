package com.recruitment.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @Value("${app.upload.resume.max-size}")
    private long resumeMaxSize;

    @Value("${app.upload.image.max-size}")
    private long imageMaxSize;

    private static final Set<String> RESUME_TYPES = Set.of("pdf", "doc", "docx");
    private static final Set<String> IMAGE_TYPES = Set.of("jpg", "jpeg", "png", "webp");

    public String storeResume(MultipartFile file, Long userId) {
        validateFile(file, RESUME_TYPES, resumeMaxSize);
        String filename = generateFilename(userId, file.getOriginalFilename(), "resumes");
        return saveFile(file, "resumes", filename);
    }

    public String storeImage(MultipartFile file, Long userId, String type) {
        validateFile(file, IMAGE_TYPES, imageMaxSize);
        String folder = "images/" + type;
        String filename = generateFilename(userId, file.getOriginalFilename(), folder);
        return saveFile(file, folder, filename);
    }

    private void validateFile(MultipartFile file, Set<String> allowedTypes, long maxSize) {
        if (file.isEmpty()) {
            throw new RuntimeException("File is empty");
        }

        if (file.getSize() > maxSize) {
            throw new RuntimeException("File size exceeds limit");
        }

        String extension = getExtension(file.getOriginalFilename());
        if (extension == null || !allowedTypes.contains(extension.toLowerCase())) {
            throw new RuntimeException("File type not allowed");
        }
    }

    private String generateFilename(Long userId, String originalName, String folder) {
        String extension = getExtension(originalName);
        return String.format("%d_%s.%s", userId, UUID.randomUUID().toString().substring(0, 8), extension);
    }

    private String saveFile(MultipartFile file, String folder, String filename) {
        try {
            Path uploadPath = Paths.get(uploadDir, folder).toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);
            
            Path targetLocation = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            return "/" + folder + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to store file", e);
        }
    }

    private String getExtension(String filename) {
        if (filename == null) return null;
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(lastDot + 1) : null;
    }

    public void deleteFile(String filePath) {
        try {
            if (filePath == null || filePath.isEmpty()) return;
            Path path = Paths.get(uploadDir, filePath).toAbsolutePath().normalize();
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // Log error but don't throw
        }
    }
}
