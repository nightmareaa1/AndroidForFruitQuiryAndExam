package com.example.userauth.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompetitionResponse DTO Tests")
class CompetitionResponseTest {

    @Test
    @DisplayName("Should create CompetitionResponse with no-args constructor")
    void shouldCreateWithNoArgsConstructor() {
        CompetitionResponse response = new CompetitionResponse();

        assertNull(response.getId());
        assertNull(response.getName());
        assertNull(response.getJudges());
        assertNull(response.getEntries());
    }

    @Test
    @DisplayName("Should create CompetitionResponse with all fields constructor")
    void shouldCreateWithAllFieldsConstructor() {
        LocalDateTime now = LocalDateTime.now();
        List<CompetitionResponse.JudgeResponse> judges = List.of();
        List<CompetitionResponse.EntryResponse> entries = List.of();

        CompetitionResponse response = new CompetitionResponse(
            1L,
            "Test Competition",
            "Description",
            1L,
            "Test Model",
            1L,
            "creator",
            now.plusDays(7),
            "ACTIVE",
            judges,
            entries,
            now,
            now
        );

        assertEquals(1L, response.getId());
        assertEquals("Test Competition", response.getName());
        assertEquals("Description", response.getDescription());
        assertEquals(1L, response.getModelId());
        assertEquals("Test Model", response.getModelName());
        assertEquals(1L, response.getCreatorId());
        assertEquals("creator", response.getCreatorUsername());
        assertEquals("ACTIVE", response.getStatus());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        CompetitionResponse response = new CompetitionResponse();
        LocalDateTime now = LocalDateTime.now();

        response.setId(2L);
        response.setName("Updated");
        response.setDescription("Updated Desc");
        response.setModelId(2L);
        response.setModelName("Model");
        response.setCreatorId(2L);
        response.setCreatorUsername("user");
        response.setDeadline(now);
        response.setStatus("ENDED");
        response.setCreatedAt(now);
        response.setUpdatedAt(now);

        assertEquals(2L, response.getId());
        assertEquals("Updated", response.getName());
        assertEquals("ENDED", response.getStatus());
    }

    @Test
    @DisplayName("Should manage judges list")
    void shouldManageJudgesList() {
        CompetitionResponse response = new CompetitionResponse();
        CompetitionResponse.JudgeResponse judge = new CompetitionResponse.JudgeResponse();
        judge.setId(1L);
        judge.setUsername("judge1");

        response.setJudges(List.of(judge));

        assertEquals(1, response.getJudges().size());
        assertEquals("judge1", response.getJudges().get(0).getUsername());
    }

    @Test
    @DisplayName("Should manage entries list")
    void shouldManageEntriesList() {
        CompetitionResponse response = new CompetitionResponse();
        CompetitionResponse.EntryResponse entry = new CompetitionResponse.EntryResponse();
        entry.setId(1L);
        entry.setEntryName("Entry 1");

        response.setEntries(List.of(entry));

        assertEquals(1, response.getEntries().size());
        assertEquals("Entry 1", response.getEntries().get(0).getEntryName());
    }

    @Test
    @DisplayName("Should test JudgeResponse")
    void shouldTestJudgeResponse() {
        CompetitionResponse.JudgeResponse judge = new CompetitionResponse.JudgeResponse();
        LocalDateTime now = LocalDateTime.now();

        judge.setId(1L);
        judge.setUserId(2L);
        judge.setUsername("judge");
        judge.setCreatedAt(now);

        assertEquals(1L, judge.getId());
        assertEquals(2L, judge.getUserId());
        assertEquals("judge", judge.getUsername());
        assertEquals(now, judge.getCreatedAt());
    }

    @Test
    @DisplayName("Should test EntryResponse")
    void shouldTestEntryResponse() {
        CompetitionResponse.EntryResponse entry = new CompetitionResponse.EntryResponse();
        LocalDateTime now = LocalDateTime.now();

        entry.setId(1L);
        entry.setEntryName("Test Entry");
        entry.setDescription("Desc");
        entry.setFilePath("/path/file.jpg");
        entry.setDisplayOrder(1);
        entry.setStatus("PENDING");
        entry.setContestantId(2L);
        entry.setContestantName("User");
        entry.setCreatedAt(now);
        entry.setUpdatedAt(now);

        assertEquals(1L, entry.getId());
        assertEquals("Test Entry", entry.getEntryName());
        assertEquals("PENDING", entry.getStatus());
        assertEquals("User", entry.getContestantName());
    }
}
