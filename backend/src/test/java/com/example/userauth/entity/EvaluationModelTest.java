package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("EvaluationModel Entity Tests")
class EvaluationModelTest {

    @Test
    @DisplayName("Should create EvaluationModel with no-args constructor")
    void shouldCreateEvaluationModelWithNoArgsConstructor() {
        EvaluationModel model = new EvaluationModel();

        assertNull(model.getId());
        assertNull(model.getName());
    }

    @Test
    @DisplayName("Should create EvaluationModel with name constructor")
    void shouldCreateEvaluationModelWithName() {
        EvaluationModel model = new EvaluationModel("Test Model");

        assertEquals("Test Model", model.getName());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        EvaluationModel model = new EvaluationModel();
        LocalDateTime now = LocalDateTime.now();

        model.setId(1L);
        model.setName("Test Model");
        model.setCreatedAt(now);
        model.setUpdatedAt(now);

        assertEquals(1L, model.getId());
        assertEquals("Test Model", model.getName());
        assertEquals(now, model.getCreatedAt());
        assertEquals(now, model.getUpdatedAt());
    }

    @Test
    @DisplayName("Should manage parameters collection")
    void shouldManageParametersCollection() {
        EvaluationModel model = new EvaluationModel();
        EvaluationParameter param1 = new EvaluationParameter();
        EvaluationParameter param2 = new EvaluationParameter();

        model.setParameters(Arrays.asList(param1, param2));

        assertNotNull(model.getParameters());
        assertEquals(2, model.getParameters().size());
    }

    @Test
    @DisplayName("Should handle soft delete")
    void shouldHandleSoftDelete() {
        EvaluationModel model = new EvaluationModel();
        assertNull(model.getDeletedAt());

        LocalDateTime deletedAt = LocalDateTime.now();
        model.setDeletedAt(deletedAt);
        assertEquals(deletedAt, model.getDeletedAt());
    }
}
