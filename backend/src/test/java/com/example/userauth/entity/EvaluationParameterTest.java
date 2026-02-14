package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvaluationParameter Entity Tests")
class EvaluationParameterTest {

    @Test
    @DisplayName("Should create EvaluationParameter with no-args constructor")
    void shouldCreateEvaluationParameterWithNoArgsConstructor() {
        EvaluationParameter param = new EvaluationParameter();

        assertNull(param.getId());
        assertNull(param.getName());
    }

    @Test
    @DisplayName("Should create EvaluationParameter with fields constructor")
    void shouldCreateEvaluationParameterWithFields() {
        EvaluationParameter param = new EvaluationParameter("Taste", 40, 1);

        assertEquals("Taste", param.getName());
        assertEquals(40, param.getWeight());
        assertEquals(1, param.getDisplayOrder());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        EvaluationParameter param = new EvaluationParameter();
        EvaluationModel model = new EvaluationModel();
        LocalDateTime now = LocalDateTime.now();

        param.setId(1L);
        param.setName("Taste");
        param.setWeight(40);
        param.setDisplayOrder(1);
        param.setModel(model);
        param.setCreatedAt(now);
        param.setUpdatedAt(now);

        assertEquals(1L, param.getId());
        assertEquals("Taste", param.getName());
        assertEquals(40, param.getWeight());
        assertEquals(1, param.getDisplayOrder());
        assertEquals(model, param.getModel());
        assertEquals(now, param.getCreatedAt());
        assertEquals(now, param.getUpdatedAt());
    }
}
