package com.example.userauth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class FileStorageServiceTest {

    @TempDir
    Path tempDir;

    @Mock
    private FileValidationService fileValidationService;

    private FileStorageService fileStorageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        fileStorageService = new FileStorageService(tempDir.toString(), fileValidationService);
    }

    @Test
    void storeFile_ValidFile_ShouldStoreSuccessfully() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );
        
        doNothing().when(fileValidationService).validateFile(any(MultipartFile.class));

        // Act
        String storedFilename = fileStorageService.storeFile(file);

        // Assert
        assertNotNull(storedFilename);
        assertTrue(storedFilename.endsWith(".jpg"));
        assertTrue(fileStorageService.fileExists(storedFilename));
        
        // Verify the file content
        Path storedFilePath = fileStorageService.getFilePath(storedFilename);
        byte[] storedContent = Files.readAllBytes(storedFilePath);
        assertArrayEquals("test content".getBytes(), storedContent);
        
        verify(fileValidationService).validateFile(file);
    }

    @Test
    void storeFile_ValidationFails_ShouldThrowException() {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "test content".getBytes()
        );
        
        doThrow(new IllegalArgumentException("Invalid file type"))
            .when(fileValidationService).validateFile(any(MultipartFile.class));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> fileStorageService.storeFile(file)
        );
        
        assertEquals("Invalid file type", exception.getMessage());
        verify(fileValidationService).validateFile(file);
    }

    @Test
    void fileExists_ExistingFile_ShouldReturnTrue() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );
        
        doNothing().when(fileValidationService).validateFile(any(MultipartFile.class));
        String storedFilename = fileStorageService.storeFile(file);

        // Act & Assert
        assertTrue(fileStorageService.fileExists(storedFilename));
    }

    @Test
    void fileExists_NonExistingFile_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(fileStorageService.fileExists("nonexistent.jpg"));
    }

    @Test
    void deleteFile_ExistingFile_ShouldReturnTrue() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );
        
        doNothing().when(fileValidationService).validateFile(any(MultipartFile.class));
        String storedFilename = fileStorageService.storeFile(file);

        // Act
        boolean deleted = fileStorageService.deleteFile(storedFilename);

        // Assert
        assertTrue(deleted);
        assertFalse(fileStorageService.fileExists(storedFilename));
    }

    @Test
    void deleteFile_NonExistingFile_ShouldReturnFalse() {
        // Act & Assert
        assertFalse(fileStorageService.deleteFile("nonexistent.jpg"));
    }

    @Test
    void getFilePath_ShouldReturnCorrectPath() throws IOException {
        // Arrange
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );
        
        doNothing().when(fileValidationService).validateFile(any(MultipartFile.class));
        String storedFilename = fileStorageService.storeFile(file);

        // Act
        Path filePath = fileStorageService.getFilePath(storedFilename);

        // Assert
        assertNotNull(filePath);
        assertTrue(filePath.toString().contains(storedFilename));
        assertTrue(Files.exists(filePath));
    }
}