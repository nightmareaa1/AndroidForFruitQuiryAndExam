package com.example.userauth.service;

import com.example.userauth.dto.FruitQueryResponse;
import com.example.userauth.entity.Fruit;
import com.example.userauth.entity.FlavorData;
import com.example.userauth.entity.NutritionData;
import com.example.userauth.repository.FlavorDataRepository;
import com.example.userauth.repository.FruitRepository;
import com.example.userauth.repository.NutritionDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for FruitQueryService
 */
@ExtendWith(MockitoExtension.class)
class FruitQueryServiceTest {

    @Mock
    private FruitRepository fruitRepository;

    @Mock
    private NutritionDataRepository nutritionDataRepository;

    @Mock
    private FlavorDataRepository flavorDataRepository;

    @InjectMocks
    private FruitQueryService fruitQueryService;

    private Fruit testFruit;
    private List<NutritionData> testNutritionData;
    private List<FlavorData> testFlavorData;

    @BeforeEach
    void setUp() {
        testFruit = new Fruit("芒果");
        testFruit.setId(1L);

        // Create test nutrition data
        NutritionData nutrition1 = new NutritionData(testFruit, "热量(kcal)", new BigDecimal("60.00"));
        NutritionData nutrition2 = new NutritionData(testFruit, "蛋白质(g)", new BigDecimal("0.82"));
        testNutritionData = Arrays.asList(nutrition1, nutrition2);

        // Create test flavor data
        FlavorData flavor1 = new FlavorData(testFruit, "甜味", new BigDecimal("8.50"));
        FlavorData flavor2 = new FlavorData(testFruit, "酸味", new BigDecimal("3.20"));
        testFlavorData = Arrays.asList(flavor1, flavor2);
    }

    @Test
    void testQueryNutritionData_Success() {
        // Arrange
        when(fruitRepository.findByName("芒果")).thenReturn(Optional.of(testFruit));
        when(nutritionDataRepository.findByFruitId(1L)).thenReturn(testNutritionData);

        // Act
        FruitQueryResponse response = fruitQueryService.queryFruitData("nutrition", "芒果");

        // Assert
        assertNotNull(response);
        assertEquals("芒果", response.getFruit());
        assertEquals("nutrition", response.getType());
        assertEquals(2, response.getData().size());
        assertEquals("热量(kcal)", response.getData().get(0).getComponentName());
        assertEquals(new BigDecimal("60.00"), response.getData().get(0).getValue());
    }

    @Test
    void testQueryFlavorData_Success() {
        // Arrange
        when(fruitRepository.findByName("芒果")).thenReturn(Optional.of(testFruit));
        when(flavorDataRepository.findByFruitId(1L)).thenReturn(testFlavorData);

        // Act
        FruitQueryResponse response = fruitQueryService.queryFruitData("flavor", "芒果");

        // Assert
        assertNotNull(response);
        assertEquals("芒果", response.getFruit());
        assertEquals("flavor", response.getType());
        assertEquals(2, response.getData().size());
        assertEquals("甜味", response.getData().get(0).getComponentName());
        assertEquals(new BigDecimal("8.50"), response.getData().get(0).getValue());
    }

    @Test
    void testQueryFruitData_FruitNotFound() {
        // Arrange
        when(fruitRepository.findByName("不存在的水果")).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            fruitQueryService.queryFruitData("nutrition", "不存在的水果");
        });
        assertTrue(exception.getMessage().contains("not found"));
    }

    @Test
    void testQueryFruitData_InvalidType() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            fruitQueryService.queryFruitData("invalid", "芒果");
        });
        assertTrue(exception.getMessage().contains("Invalid query type"));
    }

    @Test
    void testQueryFruitData_NullParameters() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            fruitQueryService.queryFruitData(null, "芒果");
        });

        assertThrows(IllegalArgumentException.class, () -> {
            fruitQueryService.queryFruitData("nutrition", null);
        });
    }

    @Test
    void testFruitExists_True() {
        // Arrange
        when(fruitRepository.existsByName("芒果")).thenReturn(true);

        // Act & Assert
        assertTrue(fruitQueryService.fruitExists("芒果"));
    }

    @Test
    void testFruitExists_False() {
        // Arrange
        when(fruitRepository.existsByName("不存在的水果")).thenReturn(false);

        // Act & Assert
        assertFalse(fruitQueryService.fruitExists("不存在的水果"));
    }

    @Test
    void testGetAllFruits() {
        // Arrange
        List<Fruit> fruits = Arrays.asList(testFruit, new Fruit("香蕉"));
        when(fruitRepository.findAll()).thenReturn(fruits);

        // Act
        List<Fruit> result = fruitQueryService.getAllFruits();

        // Assert
        assertEquals(2, result.size());
        assertEquals("芒果", result.get(0).getName());
    }
}