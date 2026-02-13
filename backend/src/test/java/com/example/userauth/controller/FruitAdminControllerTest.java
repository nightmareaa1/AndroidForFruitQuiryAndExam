package com.example.userauth.controller;

import com.example.userauth.dto.FruitRequest;
import com.example.userauth.dto.FruitResponse;
import com.example.userauth.dto.NutritionDataRequest;
import com.example.userauth.dto.FlavorDataRequest;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.NutritionData;
import com.example.userauth.entity.FlavorData;
import com.example.userauth.entity.FruitFile;
import com.example.userauth.repository.FruitFileRepository;
import com.example.userauth.repository.FruitRepository;
import com.example.userauth.repository.NutritionDataRepository;
import com.example.userauth.repository.FlavorDataRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitAdminController.class)
@ContextConfiguration(classes = {FruitAdminController.class, TestConfig.class})
public class FruitAdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FruitRepository fruitRepository;

    @MockBean
    private NutritionDataRepository nutritionDataRepository;

    @MockBean
    private FlavorDataRepository flavorDataRepository;

    @MockBean
    private FruitFileRepository fruitFileRepository;

    private Fruit fruit;

    @BeforeEach
    void setup() {
        fruit = new Fruit();
        fruit.setId(1L);
        fruit.setName("Apple");
    }

    @Test
    void getAllFruits_success() throws Exception {
        when(fruitRepository.findAll()).thenReturn(List.of(fruit));
        mockMvc.perform(get("/api/admin/fruits")).andExpect(status().isOk());
    }

    @Test
    void getFruitById_found() throws Exception {
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));
        mockMvc.perform(get("/api/admin/fruits/1")).andExpect(status().isOk());
    }

    @Test
    void getFruitById_notFound() throws Exception {
        when(fruitRepository.findById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/admin/fruits/2")).andExpect(status().isNotFound());
    }

    @Test
    void createFruit_success() throws Exception {
        when(fruitRepository.existsByName("Apple")).thenReturn(false);
        Fruit saved = new Fruit(); saved.setId(2L); saved.setName("Apple");
        when(fruitRepository.save(any(Fruit.class))).thenReturn(saved);
        FruitRequest req = new FruitRequest();
        req.setName("Apple");
        req.setDescription("Fresh fruit");
        mockMvc.perform(post("/api/admin/fruits").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void createFruit_exists() throws Exception {
        when(fruitRepository.existsByName("Apple")).thenReturn(true);
        FruitRequest req = new FruitRequest();
        req.setName("Apple");
        req.setDescription("Fresh");
        mockMvc.perform(post("/api/admin/fruits").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    void updateFruit_notFound() throws Exception {
        when(fruitRepository.findById(99L)).thenReturn(Optional.empty());
        FruitRequest req = new FruitRequest();
        req.setName("Banana");
        req.setDescription("Yummy");
        mockMvc.perform(put("/api/admin/fruits/99").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
    }

    @Test
    void deleteFruit_success() throws Exception {
        when(fruitRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/admin/fruits/1")).andExpect(status().isNoContent());
    }

    @Test
    void uploadNutritionData_success() throws Exception {
        when(fruitRepository.findById(1L)).thenReturn(Optional.of(fruit));
        NutritionDataRequest req = new NutritionDataRequest();
        req.setComponentName("Calories");
        req.setComponentValue(java.math.BigDecimal.valueOf(52));
        when(nutritionDataRepository.save(any(NutritionData.class))).thenReturn(new NutritionData());
        mockMvc.perform(post("/api/admin/fruits/1/nutrition").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void addFlavorData_notFoundFruit() throws Exception {
        when(fruitRepository.findById(3L)).thenReturn(Optional.empty());
        FlavorDataRequest req = new FlavorDataRequest();
        req.setComponentName("Sweetness");
        req.setComponentValue(java.math.BigDecimal.valueOf(1));
        mockMvc.perform(post("/api/admin/fruits/3/flavor").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isNotFound());
    }
}
