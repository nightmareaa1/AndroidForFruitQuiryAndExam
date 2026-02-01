package com.example.userauth.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class FileValidationService {

    private final long maxFileSize;
    private final Set<String> allowedTypes;

    public FileValidationService(
            @Value("${app.file.max-size}") String maxSize,
            @Value("${app.file.allowed-types}") String allowedTypesStr) {
        
        this.maxFileSize = parseFileSize(maxSize);
        this.allowedTypes = Arrays.stream(allowedTypesStr.toLowerCase().split(","))
                .map(String::trim)
                .collect(Collectors.toSet());
    }

    /**
     * Validate file format, size, and other constraints
     */
    public void validateFile(MultipartFile file) {
        if (file == null) {
            throw new IllegalArgumentException("File cannot be null");
        }

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        validateFileSize(file);
        validateFileType(file);
        validateFileName(file);
    }

    /**
     * Validate file size
     */
    private void validateFileSize(MultipartFile file) {
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException(
                String.format("File size (%d bytes) exceeds maximum allowed size (%d bytes)", 
                    file.getSize(), maxFileSize));
        }
    }

    /**
     * Validate file type based on extension
     */
    private void validateFileType(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name is invalid");
        }

        String fileExtension = getFileExtension(originalFilename).toLowerCase();
        if (fileExtension.isEmpty()) {
            throw new IllegalArgumentException("File must have an extension");
        }

        if (!allowedTypes.contains(fileExtension)) {
            throw new IllegalArgumentException(
                String.format("File type '%s' not allowed. Allowed types: %s", 
                    fileExtension, String.join(", ", allowedTypes)));
        }
    }

    /**
     * Validate file name
     */
    private void validateFileName(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.trim().isEmpty()) {
            throw new IllegalArgumentException("File name cannot be empty");
        }

        // Check for potentially dangerous characters
        if (originalFilename.contains("..") || originalFilename.contains("/") || originalFilename.contains("\\")) {
            throw new IllegalArgumentException("File name contains invalid characters");
        }
    }

    /**
     * Check if file type is allowed
     */
    public boolean isFileTypeAllowed(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            return false;
        }
        
        String extension = getFileExtension(filename).toLowerCase();
        return allowedTypes.contains(extension);
    }

    /**
     * Get maximum allowed file size in bytes
     */
    public long getMaxFileSize() {
        return maxFileSize;
    }

    /**
     * Get allowed file types
     */
    public Set<String> getAllowedTypes() {
        return allowedTypes;
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

    /**
     * Parse file size string (e.g., "10MB") to bytes
     */
    private long parseFileSize(String sizeStr) {
        if (sizeStr == null || sizeStr.trim().isEmpty()) {
            return 10 * 1024 * 1024; // Default 10MB
        }
        
        sizeStr = sizeStr.trim().toUpperCase();
        long multiplier = 1;
        
        if (sizeStr.endsWith("KB")) {
            multiplier = 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("MB")) {
            multiplier = 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        } else if (sizeStr.endsWith("GB")) {
            multiplier = 1024 * 1024 * 1024;
            sizeStr = sizeStr.substring(0, sizeStr.length() - 2);
        }
        
        try {
            return Long.parseLong(sizeStr.trim()) * multiplier;
        } catch (NumberFormatException e) {
            return 10 * 1024 * 1024; // Default 10MB
        }
    }
}