package com.example.userauth.controller;

import com.example.userauth.service.FileStorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FileController.class)
@ContextConfiguration(classes = {FileController.class, TestConfig.class})
public class FileControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FileStorageService fileStorageService;

    private MockMultipartFile mockFile;

    @BeforeEach
    void setup() {
        mockFile = new MockMultipartFile("file", "hello.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());
    }

    @Test
    void uploadFile_success() throws Exception {
        when(fileStorageService.storeFile(any())).thenReturn("hello.txt");
        mockMvc.perform(multipart("/api/files/upload").file(mockFile)).andExpect(status().isOk());
    }

    @Test
    void uploadFile_invalid() throws Exception {
        when(fileStorageService.storeFile(any())).thenThrow(new IllegalArgumentException("Invalid"));
        mockMvc.perform(multipart("/api/files/upload").file(mockFile)).andExpect(status().isBadRequest());
    }

    @Test
    void downloadFile_notFound() throws Exception {
        when(fileStorageService.fileExists("missing.txt")).thenReturn(false);
        mockMvc.perform(get("/api/files/missing.txt")).andExpect(status().isNotFound());
    }

    @Test
    void getFileInfo_notFound() throws Exception {
        when(fileStorageService.fileExists("missing.txt")).thenReturn(false);
        mockMvc.perform(get("/api/files/missing.txt/info")).andExpect(status().isNotFound());
    }
}
