package com.example.userauth.controller;

import com.example.userauth.config.TestConfig;
import com.example.userauth.dto.FruitQueryResponse;
import com.example.userauth.service.FruitQueryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FruitController.class)
@ContextConfiguration(classes = {FruitController.class, TestConfig.class})
@DisplayName("FruitController Tests")
class FruitControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private FruitQueryService fruitQueryService;

    private FruitQueryResponse nutritionResponse;
    private FruitQueryResponse flavorResponse;

    @BeforeEach
    void setUp() {
        List<FruitQueryResponse.QueryDataItem> nutritionData = List.of(
            new FruitQueryResponse.QueryDataItem("维生素C", new BigDecimal("36.0")),
            new FruitQueryResponse.QueryDataItem("钾", new BigDecimal("168.0"))
        );
        nutritionResponse = new FruitQueryResponse("芒果", "nutrition", nutritionData);

        List<FruitQueryResponse.QueryDataItem> flavorData = List.of(
            new FruitQueryResponse.QueryDataItem("甜度", new BigDecimal("15.0")),
            new FruitQueryResponse.QueryDataItem("酸度", new BigDecimal("3.0"))
        );
        flavorResponse = new FruitQueryResponse("芒果", "flavor", flavorData);
    }

    @Test
    @DisplayName("Should query fruit nutrition data successfully")
    void queryFruitData_NutritionSuccess() throws Exception {
        when(fruitQueryService.queryFruitData("nutrition", "芒果")).thenReturn(nutritionResponse);

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fruit").value("芒果"))
                .andExpect(jsonPath("$.type").value("nutrition"))
                .andExpect(jsonPath("$.data[0].componentName").value("维生素C"))
                .andExpect(jsonPath("$.data[0].value").value(36.0));

        verify(fruitQueryService).queryFruitData("nutrition", "芒果");
    }

    @Test
    @DisplayName("Should query fruit flavor data successfully")
    void queryFruitData_FlavorSuccess() throws Exception {
        when(fruitQueryService.queryFruitData("flavor", "芒果")).thenReturn(flavorResponse);

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "flavor")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fruit").value("芒果"))
                .andExpect(jsonPath("$.type").value("flavor"))
                .andExpect(jsonPath("$.data[0].componentName").value("甜度"))
                .andExpect(jsonPath("$.data[0].value").value(15.0));

        verify(fruitQueryService).queryFruitData("flavor", "芒果");
    }

    @Test
    @DisplayName("Should return 400 when type parameter is missing")
    void queryFruitData_MissingType() throws Exception {
        mockMvc.perform(get("/api/fruit/query")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fruitQueryService);
    }

    @Test
    @DisplayName("Should return 400 when fruit parameter is missing")
    void queryFruitData_MissingFruit() throws Exception {
        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fruitQueryService);
    }

    @Test
    @DisplayName("Should return 400 when type parameter is empty")
    void queryFruitData_EmptyType() throws Exception {
        mockMvc.perform(get("/api/fruit/query")
                .param("type", "")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fruitQueryService);
    }

    @Test
    @DisplayName("Should return 400 when fruit parameter is empty")
    void queryFruitData_EmptyFruit() throws Exception {
        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .param("fruit", "")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fruitQueryService);
    }

    @Test
    @DisplayName("Should return 400 for invalid query type")
    void queryFruitData_InvalidType() throws Exception {
        mockMvc.perform(get("/api/fruit/query")
                .param("type", "invalid")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(fruitQueryService);
    }

    @Test
    @DisplayName("Should return 404 when fruit not found")
    void queryFruitData_FruitNotFound() throws Exception {
        when(fruitQueryService.queryFruitData(anyString(), anyString()))
                .thenThrow(new RuntimeException("Fruit not found: 不存在"));

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .param("fruit", "不存在")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should return 400 for invalid arguments")
    void queryFruitData_InvalidArguments() throws Exception {
        when(fruitQueryService.queryFruitData(anyString(), anyString()))
                .thenThrow(new IllegalArgumentException("Invalid fruit name"));

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should return 500 on unexpected error")
    void queryFruitData_UnexpectedError() throws Exception {
        when(fruitQueryService.queryFruitData(anyString(), anyString()))
                .thenThrow(new RuntimeException("Database error"));

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "nutrition")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    @DisplayName("Should be case insensitive for query type")
    void queryFruitData_CaseInsensitive() throws Exception {
        when(fruitQueryService.queryFruitData("NUTRITION", "芒果")).thenReturn(nutritionResponse);

        mockMvc.perform(get("/api/fruit/query")
                .param("type", "NUTRITION")
                .param("fruit", "芒果")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(fruitQueryService).queryFruitData("NUTRITION", "芒果");
    }
}
