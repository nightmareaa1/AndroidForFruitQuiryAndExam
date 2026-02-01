package com.example.userauth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;

class FileValidationServiceTest {

    private FileValidationService fileValidationService;

    @BeforeEach
    void setUp() {
        // Initialize with test configuration
        fileValidationService = new FileValidationService("10MB", "jpg,jpeg,png,webp");
    }

    @Test
    void validateFile_ValidJpgFile_ShouldPass() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );

        assertDoesNotThrow(() -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_ValidPngFile_ShouldPass() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.png", 
            "image/png", 
            "test content".getBytes()
        );

        assertDoesNotThrow(() -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_ValidWebpFile_ShouldPass() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.webp", 
            "image/webp", 
            "test content".getBytes()
        );

        assertDoesNotThrow(() -> fileValidationService.validateFile(file));
    }

    @Test
    void validateFile_InvalidFileType_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.txt", 
            "text/plain", 
            "test content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileValidationService.validateFile(file)
        );
        
        assertTrue(exception.getMessage().contains("File type 'txt' not allowed"));
    }

    @Test
    void validateFile_EmptyFile_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "test.jpg", 
            "image/jpeg", 
            new byte[0]
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileValidationService.validateFile(file)
        );
        
        assertEquals("File is empty", exception.getMessage());
    }

    @Test
    void validateFile_NullFile_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileValidationService.validateFile(null)
        );
        
        assertEquals("File cannot be null", exception.getMessage());
    }

    @Test
    void validateFile_FileWithoutExtension_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "testfile", 
            "application/octet-stream", 
            "test content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileValidationService.validateFile(file)
        );
        
        assertEquals("File must have an extension", exception.getMessage());
    }

    @Test
    void validateFile_FileWithDangerousPath_ShouldThrowException() {
        MultipartFile file = new MockMultipartFile(
            "file", 
            "../test.jpg", 
            "image/jpeg", 
            "test content".getBytes()
        );

        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class, 
            () -> fileValidationService.validateFile(file)
        );
        
        assertEquals("File name contains invalid characters", exception.getMessage());
    }

    @Test
    void isFileTypeAllowed_ValidTypes_ShouldReturnTrue() {
        assertTrue(fileValidationService.isFileTypeAllowed("test.jpg"));
        assertTrue(fileValidationService.isFileTypeAllowed("test.jpeg"));
        assertTrue(fileValidationService.isFileTypeAllowed("test.png"));
        assertTrue(fileValidationService.isFileTypeAllowed("test.webp"));
    }

    @Test
    void isFileTypeAllowed_InvalidTypes_ShouldReturnFalse() {
        assertFalse(fileValidationService.isFileTypeAllowed("test.txt"));
        assertFalse(fileValidationService.isFileTypeAllowed("test.pdf"));
        assertFalse(fileValidationService.isFileTypeAllowed("test"));
        assertFalse(fileValidationService.isFileTypeAllowed(null));
    }

    @Test
    void getMaxFileSize_ShouldReturnCorrectSize() {
        assertEquals(10 * 1024 * 1024, fileValidationService.getMaxFileSize());
    }

    @Test
    void getAllowedTypes_ShouldReturnCorrectTypes() {
        var allowedTypes = fileValidationService.getAllowedTypes();
        assertEquals(4, allowedTypes.size());
        assertTrue(allowedTypes.contains("jpg"));
        assertTrue(allowedTypes.contains("jpeg"));
        assertTrue(allowedTypes.contains("png"));
        assertTrue(allowedTypes.contains("webp"));
    }
}