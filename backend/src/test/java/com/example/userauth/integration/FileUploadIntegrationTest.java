package com.example.userauth.integration;

import com.example.userauth.service.FileStorageService;
import com.example.userauth.service.FileValidationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration test for file upload functionality
 */
class FileUploadIntegrationTest {

    @TempDir
    Path tempDir;

    @Test
    void testCompleteFileUploadFlow() throws IOException {
        // Setup services
        FileValidationService validationService = new FileValidationService("10MB", "jpg,jpeg,png,webp");
        FileStorageService storageService = new FileStorageService(tempDir.toString(), validationService);

        // Create a test file
        MockMultipartFile testFile = new MockMultipartFile(
            "file",
            "test-image.jpg",
            "image/jpeg",
            "test image content".getBytes()
        );

        // Test the complete flow
        String storedFilename = storageService.storeFile(testFile);

        // Verify file was stored
        assertNotNull(storedFilename);
        assertTrue(storedFilename.endsWith(".jpg"));
        assertTrue(storageService.fileExists(storedFilename));

        // Verify file path
        Path filePath = storageService.getFilePath(storedFilename);
        assertNotNull(filePath);
        assertTrue(filePath.toString().contains(storedFilename));

        // Test file deletion
        boolean deleted = storageService.deleteFile(storedFilename);
        assertTrue(deleted);
        assertFalse(storageService.fileExists(storedFilename));
    }

    @Test
    void testFileValidationIntegration() {
        FileValidationService validationService = new FileValidationService("10MB", "jpg,jpeg,png,webp");

        // Test valid file types
        assertTrue(validationService.isFileTypeAllowed("image.jpg"));
        assertTrue(validationService.isFileTypeAllowed("photo.png"));
        assertTrue(validationService.isFileTypeAllowed("picture.webp"));

        // Test invalid file types
        assertFalse(validationService.isFileTypeAllowed("document.pdf"));
        assertFalse(validationService.isFileTypeAllowed("text.txt"));

        // Test file size limits
        assertEquals(10 * 1024 * 1024, validationService.getMaxFileSize());

        // Test allowed types
        assertEquals(4, validationService.getAllowedTypes().size());
    }

    @Test
    void testInvalidFileRejection() {
        FileValidationService validationService = new FileValidationService("10MB", "jpg,jpeg,png,webp");
        FileStorageService storageService = new FileStorageService(tempDir.toString(), validationService);

        // Test invalid file type
        MockMultipartFile invalidFile = new MockMultipartFile(
            "file",
            "document.txt",
            "text/plain",
            "some text content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> storageService.storeFile(invalidFile)
        );

        assertTrue(exception.getMessage().contains("File type 'txt' not allowed"));
    }
}