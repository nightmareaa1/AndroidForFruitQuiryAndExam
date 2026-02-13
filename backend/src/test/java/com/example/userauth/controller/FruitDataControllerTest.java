package com.example.userauth.controller;

import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FruitData;
import com.example.userauth.entity.FruitDataField;
import com.example.userauth.repository.FruitDataFieldRepository;
import com.example.userauth.repository.FruitDataRepository;
import com.example.userauth.repository.FruitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import java.util.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitDataController.class)
@ContextConfiguration(classes = {FruitDataController.class, TestConfig.class})
public class FruitDataControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FruitRepository fruitRepository;

    @MockBean
    private FruitDataRepository fruitDataRepository;

    @MockBean
    private FruitDataFieldRepository fieldRepository;

    private Fruit fruit;
    private FruitData data;

    @BeforeEach
    void setup() {
        fruit = new Fruit();
        fruit.setId(1L);
        fruit.setName("Apple");
        data = new FruitData();
        data.setFruitName("Apple");
        data.setDataType("nutrition");
        data.setDataValues(new HashMap<>());
    }

    @Test
    void getFruits_success() throws Exception {
        when(fruitRepository.findAll()).thenReturn(List.of(fruit));
        mockMvc.perform(get("/api/fruit-data/fruits")).andExpect(status().isOk());
    }

    @Test
    void getFields_success() throws Exception {
        FruitDataField f = new FruitDataField();
        f.setId(1L);
        f.setFieldName("Calories");
        f.setFieldType("nutrition");
        f.setDisplayOrder(0);
        f.setIsActive(true);
        when(fieldRepository.findByFieldTypeAndIsActiveTrueOrderByDisplayOrder("nutrition")).thenReturn(List.of(f));
        mockMvc.perform(get("/api/fruit-data/fields/nutrition")).andExpect(status().isOk());
    }

    @Test
    void query_fruitNotFound() throws Exception {
        when(fruitRepository.findByName("Banana")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/fruit-data/query?fruit=Banana&dataType=nutrition")).andExpect(status().isNotFound());
    }

    @Test
    void query_fruitHasData() throws Exception {
        when(fruitRepository.findByName("Apple")).thenReturn(Optional.of(fruit));
        when(fruitDataRepository.findByFruitNameAndDataType("Apple","nutrition")).thenReturn(Optional.of(data));
        mockMvc.perform(get("/api/fruit-data/query?fruit=Apple&dataType=nutrition")).andExpect(status().isOk());
    }

    @Test
    void query_fruitNoData() throws Exception {
        when(fruitRepository.findByName("Apple")).thenReturn(Optional.of(fruit));
        when(fruitDataRepository.findByFruitNameAndDataType("Apple","nutrition")).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/fruit-data/query?fruit=Apple&dataType=nutrition")).andExpect(status().isOk());
    }

    @Test
    void getAllData_success() throws Exception {
        when(fruitRepository.findAll()).thenReturn(List.of(fruit));
        when(fruitDataRepository.findByFruitNameAndDataType("Apple","nutrition")).thenReturn(Optional.of(data));
        mockMvc.perform(get("/api/fruit-data/all/nutrition")).andExpect(status().isOk());
    }
}
