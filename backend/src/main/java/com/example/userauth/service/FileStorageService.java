package com.example.userauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path uploadPath;
    private final FileValidationService fileValidationService;

    public FileStorageService(
            @Value("${app.file.upload-dir}") String uploadDir,
            FileValidationService fileValidationService) {
        
        this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        this.fileValidationService = fileValidationService;
        
        try {
            Files.createDirectories(this.uploadPath);
        } catch (IOException ex) {
            throw new RuntimeException("Could not create upload directory: " + uploadDir, ex);
        }
    }

    /**
     * Store a file and return the generated filename
     */
    public String storeFile(MultipartFile file) throws IOException {
        fileValidationService.validateFile(file);
        
        String originalFilename = file.getOriginalFilename();
        String fileExtension = getFileExtension(originalFilename);
        String uniqueFilename = generateUniqueFilename(fileExtension);
        
        Path targetLocation = this.uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        
        return uniqueFilename;
    }

    /**
     * Get the path to a stored file
     */
    public Path getFilePath(String filename) {
        return this.uploadPath.resolve(filename).normalize();
    }

    /**
     * Check if a file exists
     */
    public boolean fileExists(String filename) {
        Path filePath = getFilePath(filename);
        return Files.exists(filePath) && Files.isRegularFile(filePath);
    }

    /**
     * Delete a file
     */
    public boolean deleteFile(String filename) {
        try {
            Path filePath = getFilePath(filename);
            return Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            return false;
        }
    }

    /**
     * Generate a unique filename with the original extension
     */
    private String generateUniqueFilename(String extension) {
        return UUID.randomUUID().toString() + "." + extension;
    }

    /**
     * Extract file extension from filename
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf('.') == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}