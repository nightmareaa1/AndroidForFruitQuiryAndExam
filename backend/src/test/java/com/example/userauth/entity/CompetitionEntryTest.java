package com.example.userauth.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CompetitionEntry Entity Tests")
class CompetitionEntryTest {

    @Test
    @DisplayName("Should create CompetitionEntry with no-args constructor")
    void shouldCreateCompetitionEntryWithNoArgsConstructor() {
        CompetitionEntry entry = new CompetitionEntry();

        assertNull(entry.getId());
        assertNull(entry.getEntryName());
        assertNull(entry.getDescription());
    }

    @Test
    @DisplayName("Should create CompetitionEntry with required fields constructor")
    void shouldCreateCompetitionEntryWithRequiredFields() {
        Competition competition = new Competition();

        CompetitionEntry entry = new CompetitionEntry(
            competition,
            "Test Entry",
            "Test Description",
            "/path/to/file.jpg",
            1
        );

        assertEquals(competition, entry.getCompetition());
        assertEquals("Test Entry", entry.getEntryName());
        assertEquals("Test Description", entry.getDescription());
        assertEquals("/path/to/file.jpg", entry.getFilePath());
        assertEquals(1, entry.getDisplayOrder());
        assertEquals(CompetitionEntry.EntryStatus.PENDING, entry.getStatus());
    }

    @Test
    @DisplayName("Should set and get all fields")
    void shouldSetAndGetAllFields() {
        CompetitionEntry entry = new CompetitionEntry();
        LocalDateTime now = LocalDateTime.now();
        Competition competition = new Competition();
        User contestant = new User();
        contestant.setUsername("testuser");

        entry.setId(1L);
        entry.setCompetition(competition);
        entry.setEntryName("Test Entry");
        entry.setDescription("Test Description");
        entry.setFilePath("/path/file.jpg");
        entry.setDisplayOrder(2);
        entry.setStatus(CompetitionEntry.EntryStatus.APPROVED);
        entry.setContestant(contestant);
        entry.setCreatedAt(now);
        entry.setUpdatedAt(now);

        assertEquals(1L, entry.getId());
        assertEquals(competition, entry.getCompetition());
        assertEquals("Test Entry", entry.getEntryName());
        assertEquals("Test Description", entry.getDescription());
        assertEquals("/path/file.jpg", entry.getFilePath());
        assertEquals(2, entry.getDisplayOrder());
        assertEquals(CompetitionEntry.EntryStatus.APPROVED, entry.getStatus());
        assertEquals(contestant, entry.getContestant());
    }

    @Test
    @DisplayName("Should manage ratings collection")
    void shouldManageRatingsCollection() {
        CompetitionEntry entry = new CompetitionEntry();
        CompetitionRating rating1 = new CompetitionRating();
        CompetitionRating rating2 = new CompetitionRating();

        entry.setRatings(Arrays.asList(rating1, rating2));

        assertNotNull(entry.getRatings());
        assertEquals(2, entry.getRatings().size());
    }

    @Test
    @DisplayName("Should get contestant name when contestant exists")
    void shouldGetContestantNameWhenContestantExists() {
        CompetitionEntry entry = new CompetitionEntry();
        User contestant = new User();
        contestant.setUsername("john_doe");
        entry.setContestant(contestant);

        assertEquals("john_doe", entry.getContestantName());
    }

    @Test
    @DisplayName("Should return unknown when contestant is null")
    void shouldReturnUnknownWhenContestantIsNull() {
        CompetitionEntry entry = new CompetitionEntry();
        entry.setContestant(null);

        assertEquals("未知参赛者", entry.getContestantName());
    }

    @Test
    @DisplayName("Should check if entry is pending")
    void shouldCheckIfEntryIsPending() {
        CompetitionEntry pendingEntry = new CompetitionEntry();
        pendingEntry.setStatus(CompetitionEntry.EntryStatus.PENDING);

        CompetitionEntry approvedEntry = new CompetitionEntry();
        approvedEntry.setStatus(CompetitionEntry.EntryStatus.APPROVED);

        assertTrue(pendingEntry.isPending());
        assertFalse(approvedEntry.isPending());
    }

    @Test
    @DisplayName("Should check if entry is approved")
    void shouldCheckIfEntryIsApproved() {
        CompetitionEntry approvedEntry = new CompetitionEntry();
        approvedEntry.setStatus(CompetitionEntry.EntryStatus.APPROVED);

        CompetitionEntry pendingEntry = new CompetitionEntry();
        pendingEntry.setStatus(CompetitionEntry.EntryStatus.PENDING);

        assertTrue(approvedEntry.isApproved());
        assertFalse(pendingEntry.isApproved());
    }

    @Test
    @DisplayName("Should check if entry is rejected")
    void shouldCheckIfEntryIsRejected() {
        CompetitionEntry rejectedEntry = new CompetitionEntry();
        rejectedEntry.setStatus(CompetitionEntry.EntryStatus.REJECTED);

        CompetitionEntry pendingEntry = new CompetitionEntry();
        pendingEntry.setStatus(CompetitionEntry.EntryStatus.PENDING);

        assertTrue(rejectedEntry.isRejected());
        assertFalse(pendingEntry.isRejected());
    }

    @Test
    @DisplayName("Should handle soft delete")
    void shouldHandleSoftDelete() {
        CompetitionEntry entry = new CompetitionEntry();
        assertFalse(entry.isDeleted());

        entry.setDeletedAt(LocalDateTime.now());
        assertTrue(entry.isDeleted());
    }

    @Test
    @DisplayName("Should handle EntryStatus enum")
    void shouldHandleEntryStatusEnum() {
        assertNotNull(CompetitionEntry.EntryStatus.PENDING);
        assertNotNull(CompetitionEntry.EntryStatus.APPROVED);
        assertNotNull(CompetitionEntry.EntryStatus.REJECTED);
    }
}
