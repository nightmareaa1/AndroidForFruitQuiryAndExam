package com.example.userauth.controller;

import com.example.userauth.dto.*;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FruitData;
import com.example.userauth.entity.FruitDataField;
import com.example.userauth.repository.FruitDataFieldRepository;
import com.example.userauth.repository.FruitDataRepository;
import com.example.userauth.repository.FruitRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitDataAdminController.class)
@ContextConfiguration(classes = {FruitDataAdminController.class, TestConfig.class})
public class FruitDataAdminControllerTest {
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
    private FruitDataField field;

    @BeforeEach
    void setup() {
        fruit = new Fruit();
        fruit.setId(1L);
        fruit.setName("Apple");
        field = new FruitDataField();
        field.setId(1L);
        field.setFieldType("nutrition");
        field.setFieldName("Calories");
        field.setFieldUnit("kcal");
    }

    @Test
    void getAllDataTypes_success() throws Exception {
        when(fieldRepository.findAll()).thenReturn(List.of(field));
        mockMvc.perform(get("/api/admin/fruit-data/data-types")).andExpect(status().isOk());
    }

    @Test
    void addDataType_success() throws Exception {
        FruitDataAdminController.DataTypeCreateRequest req = new FruitDataAdminController.DataTypeCreateRequest();
        req.setDataType("nutrition");
        req.setFirstFieldName("Calories");
        req.setFirstFieldUnit("kcal");
        when(fieldRepository.findByFieldTypeAndIsActiveTrueOrderByDisplayOrder("nutrition")).thenReturn(List.of());
        FruitDataField saved = new FruitDataField(); saved.setId(2L);
        when(fieldRepository.save(any(FruitDataField.class))).thenReturn(saved);
        mockMvc.perform(post("/api/admin/fruit-data/data-types").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void addDataType_exists() throws Exception {
        FruitDataAdminController.DataTypeCreateRequest req = new FruitDataAdminController.DataTypeCreateRequest();
        req.setDataType("nutrition");
        req.setFirstFieldName("Calories");
        req.setFirstFieldUnit("kcal");
        when(fieldRepository.findByFieldTypeAndIsActiveTrueOrderByDisplayOrder("nutrition")).thenReturn(List.of(field));
        mockMvc.perform(post("/api/admin/fruit-data/data-types").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

    @Test
    void deleteDataType_success() throws Exception {
        when(fieldRepository.findAll()).thenReturn(List.of(field));
        mockMvc.perform(delete("/api/admin/fruit-data/data-types/nutrition")).andExpect(status().isOk());
    }

    @Test
    void getTableData_success() throws Exception {
        FruitData data = new FruitData(); data.setFruitName("Apple"); data.setDataType("nutrition");
        when(fruitDataRepository.findByFruitNameAndDataType("Apple","nutrition")).thenReturn(Optional.of(data));
        mockMvc.perform(get("/api/admin/fruit-data/data?fruitName=Apple&dataType=nutrition")).andExpect(status().isOk());
    }

    @Test
    void addOrUpdateData_create() throws Exception {
        FruitDataAdminController.FruitDataRequest req = new FruitDataAdminController.FruitDataRequest();
        req.setFruitName("Apple"); req.setDataType("nutrition"); req.setFieldName("Calories"); req.setValue(12.0);
        when(fruitRepository.findByName("Apple")).thenReturn(Optional.of(fruit));
        when(fruitDataRepository.findByFruitNameAndDataType("Apple","nutrition")).thenReturn(Optional.empty());
        FruitData saved = new FruitData(); saved.setFruitName("Apple"); saved.setDataType("nutrition");
        when(fruitDataRepository.save(any(FruitData.class))).thenReturn(saved);
        mockMvc.perform(post("/api/admin/fruit-data/data").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());
    }

    @Test
    void deleteField_success() throws Exception {
        when(fieldRepository.existsById(1L)).thenReturn(true);
        mockMvc.perform(delete("/api/admin/fruit-data/fields/1")).andExpect(status().isNoContent());
    }

    @Test
    void deleteTable_success() throws Exception {
        // Ensure the repository returns an existing table for deletion
        FruitData data = new FruitData();
        data.setFruitName("Apple");
        data.setDataType("nutrition");
        when(fruitDataRepository.findByFruitNameAndDataType("Apple", "nutrition")).thenReturn(Optional.of(data));
        mockMvc.perform(delete("/api/admin/fruit-data/table?fruitName=Apple&dataType=nutrition")).andExpect(status().isOk());
    }
}
