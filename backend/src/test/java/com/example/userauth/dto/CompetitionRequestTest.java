package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompetitionRequest DTO Tests")
class CompetitionRequestTest {

    @Test
    @DisplayName("Should create CompetitionRequest with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        CompetitionRequest request = new CompetitionRequest();

        assertNull(request.getName());
        assertNull(request.getDescription());
        assertNull(request.getModelId());
        assertNull(request.getDeadline());
        assertNull(request.getJudgeIds());
    }

    @Test
    @DisplayName("Should create CompetitionRequest with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        LocalDateTime deadline = LocalDateTime.now().plusDays(7);
        List<Long> judgeIds = List.of(1L, 2L);

        CompetitionRequest request = new CompetitionRequest(
            "Test Competition",
            "Test Description",
            1L,
            deadline,
            judgeIds
        );

        assertEquals("Test Competition", request.getName());
        assertEquals("Test Description", request.getDescription());
        assertEquals(1L, request.getModelId());
        assertEquals(deadline, request.getDeadline());
        assertEquals(judgeIds, request.getJudgeIds());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        CompetitionRequest request = new CompetitionRequest();
        LocalDateTime deadline = LocalDateTime.now();
        List<Long> judgeIds = List.of(3L);

        request.setName("New Competition");
        request.setDescription("New Description");
        request.setModelId(2L);
        request.setDeadline(deadline);
        request.setJudgeIds(judgeIds);

        assertEquals("New Competition", request.getName());
        assertEquals("New Description", request.getDescription());
        assertEquals(2L, request.getModelId());
        assertEquals(deadline, request.getDeadline());
        assertEquals(judgeIds, request.getJudgeIds());
    }
}
