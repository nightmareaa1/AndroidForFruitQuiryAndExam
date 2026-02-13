package com.example.userauth.service;

import com.example.userauth.dto.CompetitionRequest;
import com.example.userauth.dto.CompetitionResponse;
import com.example.userauth.dto.EntryRequest;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CompetitionService Tests")
class CompetitionServiceTest {

    @Mock
    private CompetitionRepository competitionRepository;

    @Mock
    private CompetitionJudgeRepository judgeRepository;

    @Mock
    private CompetitionEntryRepository entryRepository;

    @Mock
    private EvaluationModelRepository modelRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private FileStorageService fileStorageService;

    @InjectMocks
    private CompetitionService competitionService;

    private Competition competition;
    private Competition competition2;
    private User creator;
    private User judge;
    private User admin;
    private EvaluationModel model;
    private CompetitionEntry entry;
    private CompetitionJudge competitionJudge;

    @BeforeEach
    void setUp() {
        creator = new User();
        creator.setId(1L);
        creator.setUsername("creator");
        creator.setIsAdmin(false);

        judge = new User();
        judge.setId(2L);
        judge.setUsername("judge");
        judge.setIsAdmin(false);

        admin = new User();
        admin.setId(3L);
        admin.setUsername("admin");
        admin.setIsAdmin(true);

        model = new EvaluationModel();
        model.setId(1L);
        model.setName("测试模型");

        competition = new Competition();
        competition.setId(1L);
        competition.setName("测试赛事");
        competition.setDescription("测试描述");
        competition.setModel(model);
        competition.setCreator(creator);
        competition.setDeadline(LocalDateTime.now().plusDays(7));
        competition.setStatus(Competition.CompetitionStatus.ACTIVE);
        competition.setCreatedAt(LocalDateTime.now());
        competition.setUpdatedAt(LocalDateTime.now());

        competition2 = new Competition();
        competition2.setId(2L);
        competition2.setName("测试赛事2");
        competition2.setModel(model);
        competition2.setCreator(creator);
        competition2.setDeadline(LocalDateTime.now().plusDays(14));
        competition2.setStatus(Competition.CompetitionStatus.ACTIVE);

        competitionJudge = new CompetitionJudge();
        competitionJudge.setId(1L);
        competitionJudge.setCompetition(competition);
        competitionJudge.setJudge(judge);
        competitionJudge.setCreatedAt(LocalDateTime.now());

        entry = new CompetitionEntry();
        entry.setId(1L);
        entry.setEntryName("测试作品");
        entry.setDescription("作品描述");
        entry.setCompetition(competition);
        entry.setContestant(creator);
        entry.setStatus(CompetitionEntry.EntryStatus.PENDING);
        entry.setDisplayOrder(1);
        entry.setCreatedAt(LocalDateTime.now());
        entry.setUpdatedAt(LocalDateTime.now());
    }

    // ==================== Query Tests ====================

    @Test
    @DisplayName("Should get all competitions successfully")
    void getAllCompetitions_Success() {
        // Given
        when(competitionRepository.findAll()).thenReturn(Arrays.asList(competition, competition2));

        // When
        List<CompetitionResponse> responses = competitionService.getAllCompetitions(1L, false);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("测试赛事", responses.get(0).getName());
        verify(competitionRepository).findAll();
    }

    @Test
    @DisplayName("Should get all competitions for admin")
    void getAllCompetitions_Admin() {
        // Given
        when(competitionRepository.findAll()).thenReturn(Arrays.asList(competition));

        // When
        List<CompetitionResponse> responses = competitionService.getAllCompetitions(1L, true);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(competitionRepository).findAll();
    }

    @Test
    @DisplayName("Should get competitions by creator")
    void getCompetitionsByCreator_Success() {
        // Given
        when(competitionRepository.findByCreatorIdOrderByCreatedAtDesc(1L))
                .thenReturn(Arrays.asList(competition, competition2));

        // When
        List<CompetitionResponse> responses = competitionService.getCompetitionsByCreator(1L);

        // Then
        assertNotNull(responses);
        assertEquals(2, responses.size());
        verify(competitionRepository).findByCreatorIdOrderByCreatedAtDesc(1L);
    }

    @Test
    @DisplayName("Should return empty list when creator has no competitions")
    void getCompetitionsByCreator_Empty() {
        // Given
        when(competitionRepository.findByCreatorIdOrderByCreatedAtDesc(99L))
                .thenReturn(Collections.emptyList());

        // When
        List<CompetitionResponse> responses = competitionService.getCompetitionsByCreator(99L);

        // Then
        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should get competitions by judge")
    void getCompetitionsByJudge_Success() {
        // Given
        when(competitionRepository.findByJudgeIdOrderByCreatedAtDesc(2L))
                .thenReturn(Arrays.asList(competition));

        // When
        List<CompetitionResponse> responses = competitionService.getCompetitionsByJudge(2L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("测试赛事", responses.get(0).getName());
        verify(competitionRepository).findByJudgeIdOrderByCreatedAtDesc(2L);
    }

    @Test
    @DisplayName("Should get competition by id successfully")
    void getCompetitionById_Success() {
        // Given
        when(competitionRepository.findByIdWithDetails(1L)).thenReturn(Optional.of(competition));

        // When
        Optional<CompetitionResponse> response = competitionService.getCompetitionById(1L);

        // Then
        assertTrue(response.isPresent());
        assertEquals("测试赛事", response.get().getName());
        assertEquals(1L, response.get().getId());
        verify(competitionRepository).findByIdWithDetails(1L);
    }

    @Test
    @DisplayName("Should return empty when competition not found")
    void getCompetitionById_NotFound() {
        // Given
        when(competitionRepository.findByIdWithDetails(99L)).thenReturn(Optional.empty());

        // When
        Optional<CompetitionResponse> response = competitionService.getCompetitionById(99L);

        // Then
        assertFalse(response.isPresent());
    }

    // ==================== Create Competition Tests ====================

    @Test
    @DisplayName("Should create competition successfully")
    void createCompetition_Success() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(anyLong(), eq(1L))).thenReturn(false);
        when(judgeRepository.save(any(CompetitionJudge.class))).thenReturn(competitionJudge);

        // When
        CompetitionResponse response = competitionService.createCompetition(request, 1L);

        // Then
        assertNotNull(response);
        assertEquals("测试赛事", response.getName());
        verify(modelRepository).findById(1L);
        verify(userRepository, atLeast(1)).findById(1L);
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    @DisplayName("Should throw exception when model not found")
    void createCompetition_ModelNotFound() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        when(modelRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.createCompetition(request, 1L));
        assertTrue(exception.getMessage().contains("评价模型不存在"));
    }

    @Test
    @DisplayName("Should throw exception when creator not found")
    void createCompetition_CreatorNotFound() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.createCompetition(request, 1L));
        assertTrue(exception.getMessage().contains("用户不存在"));
    }

    @Test
    @DisplayName("Should throw exception when deadline is in the past")
    void createCompetition_DeadlineInPast() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        request.setDeadline(LocalDateTime.now().minusDays(1));

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.createCompetition(request, 1L));
        assertEquals("截止时间必须在未来", exception.getMessage());
    }

    @Test
    @DisplayName("Should create competition with additional judges")
    void createCompetition_WithAdditionalJudges() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        request.setJudgeIds(Arrays.asList(2L, 3L));

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(userRepository.findById(2L)).thenReturn(Optional.of(judge));
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(anyLong(), eq(1L))).thenReturn(false);
        when(judgeRepository.existsByCompetitionIdAndJudgeId(anyLong(), eq(2L))).thenReturn(false);
        when(judgeRepository.existsByCompetitionIdAndJudgeId(anyLong(), eq(3L))).thenReturn(false);
        when(judgeRepository.save(any(CompetitionJudge.class))).thenReturn(competitionJudge);

        // When
        CompetitionResponse response = competitionService.createCompetition(request, 1L);

        // Then
        assertNotNull(response);
        verify(judgeRepository, atLeast(1)).save(any(CompetitionJudge.class));
    }

    // ==================== Update Competition Tests ====================

    @Test
    @DisplayName("Should update competition successfully")
    void updateCompetition_Success() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        request.setName("更新后的赛事");

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        // When
        CompetitionResponse response = competitionService.updateCompetition(1L, request, 1L);

        // Then
        assertNotNull(response);
        verify(competitionRepository).save(any(Competition.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent competition")
    void updateCompetition_NotFound() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        when(competitionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.updateCompetition(99L, request, 1L));
        assertTrue(exception.getMessage().contains("赛事不存在"));
    }

    @Test
    @DisplayName("Should throw exception when non-creator tries to update")
    void updateCompetition_NotCreator() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.updateCompetition(1L, request, 99L));
        assertEquals("只有赛事创建者可以修改赛事", exception.getMessage());
    }

    @Test
    @DisplayName("Should update competition with different model")
    void updateCompetition_DifferentModel() {
        // Given
        CompetitionRequest request = createValidCompetitionRequest();
        request.setModelId(2L);

        EvaluationModel newModel = new EvaluationModel();
        newModel.setId(2L);
        newModel.setName("新模型");

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(modelRepository.findById(2L)).thenReturn(Optional.of(newModel));
        when(competitionRepository.save(any(Competition.class))).thenReturn(competition);

        // When
        CompetitionResponse response = competitionService.updateCompetition(1L, request, 1L);

        // Then
        assertNotNull(response);
        verify(modelRepository).findById(2L);
    }

    // ==================== Delete Competition Tests ====================

    @Test
    @DisplayName("Should delete competition successfully")
    void deleteCompetition_Success() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When
        competitionService.deleteCompetition(1L, 1L);

        // Then
        verify(competitionRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent competition")
    void deleteCompetition_NotFound() {
        // Given
        when(competitionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.deleteCompetition(99L, 1L));
        assertTrue(exception.getMessage().contains("赛事不存在"));
    }

    @Test
    @DisplayName("Should throw exception when non-creator tries to delete")
    void deleteCompetition_NotCreator() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.deleteCompetition(1L, 99L));
        assertEquals("只有赛事创建者可以删除赛事", exception.getMessage());
    }

    // ==================== Judge Management Tests ====================

    @Test
    @DisplayName("Should add judges to competition successfully")
    void addJudgesToCompetition_Success() {
        // Given
        List<Long> judgeIds = Arrays.asList(2L, 3L);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(2L)).thenReturn(Optional.of(judge));
        when(userRepository.findById(3L)).thenReturn(Optional.of(admin));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(anyLong(), anyLong())).thenReturn(false);
        when(judgeRepository.save(any(CompetitionJudge.class))).thenReturn(competitionJudge);

        // When
        competitionService.addJudgesToCompetition(1L, judgeIds);

        // Then
        verify(judgeRepository, times(2)).save(any(CompetitionJudge.class));
    }

    @Test
    @DisplayName("Should skip adding existing judges")
    void addJudgesToCompetition_SkipExisting() {
        // Given
        List<Long> judgeIds = Arrays.asList(2L);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(2L)).thenReturn(Optional.of(judge));
        when(judgeRepository.existsByCompetitionIdAndJudgeId(1L, 2L)).thenReturn(true);

        // When
        competitionService.addJudgesToCompetition(1L, judgeIds);

        // Then
        verify(judgeRepository, never()).save(any(CompetitionJudge.class));
    }

    @Test
    @DisplayName("Should throw exception when adding judges to non-existent competition")
    void addJudgesToCompetition_CompetitionNotFound() {
        // Given
        List<Long> judgeIds = Arrays.asList(2L);
        when(competitionRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.addJudgesToCompetition(99L, judgeIds));
        assertTrue(exception.getMessage().contains("赛事不存在"));
    }

    @Test
    @DisplayName("Should throw exception when judge user not found")
    void addJudgesToCompetition_JudgeNotFound() {
        // Given
        List<Long> judgeIds = Arrays.asList(99L);
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.addJudgesToCompetition(1L, judgeIds));
        assertTrue(exception.getMessage().contains("用户不存在"));
    }

    @Test
    @DisplayName("Should remove judge from competition successfully")
    void removeJudgeFromCompetition_Success() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When
        competitionService.removeJudgeFromCompetition(1L, 2L, 1L);

        // Then
        verify(judgeRepository).deleteByCompetitionIdAndJudgeId(1L, 2L);
    }

    @Test
    @DisplayName("Should throw exception when non-creator tries to remove judge")
    void removeJudgeFromCompetition_NotCreator() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.removeJudgeFromCompetition(1L, 2L, 99L));
        assertEquals("只有赛事创建者可以移除评委", exception.getMessage());
    }

    // ==================== Entry Management Tests ====================

    @Test
    @DisplayName("Should submit entry to competition successfully")
    void submitEntryToCompetition_Success() {
        // Given
        EntryRequest request = createValidEntryRequest();
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(entryRepository.findMaxDisplayOrderByCompetitionId(1L)).thenReturn(0);
        when(entryRepository.save(any(CompetitionEntry.class))).thenReturn(entry);

        // When
        Long entryId = competitionService.submitEntryToCompetition(1L, request, null, 1L);

        // Then
        assertNotNull(entryId);
        assertEquals(1L, entryId);
        verify(entryRepository).save(any(CompetitionEntry.class));
    }

    @Test
    @DisplayName("Should submit entry with file successfully")
    void submitEntryToCompetition_WithFile() throws IOException {
        // Given
        EntryRequest request = createValidEntryRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "test".getBytes());

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(userRepository.findById(1L)).thenReturn(Optional.of(creator));
        when(entryRepository.findMaxDisplayOrderByCompetitionId(1L)).thenReturn(0);
        when(fileStorageService.storeFile(any(MultipartFile.class))).thenReturn("uploads/test.jpg");
        when(entryRepository.save(any(CompetitionEntry.class))).thenReturn(entry);

        // When
        Long entryId = competitionService.submitEntryToCompetition(1L, request, file, 1L);

        // Then
        assertNotNull(entryId);
        verify(fileStorageService).storeFile(any(MultipartFile.class));
    }

    @Test
    @DisplayName("Should throw exception when submitting entry after deadline")
    void submitEntryToCompetition_DeadlinePassed() {
        // Given
        competition.setDeadline(LocalDateTime.now().minusDays(1));
        EntryRequest request = createValidEntryRequest();

        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.submitEntryToCompetition(1L, request, null, 1L));
        assertEquals("赛事截止时间已过，无法提交参赛作品", exception.getMessage());
    }

    @Test
    @DisplayName("Should get competition entries successfully")
    void getCompetitionEntries_Success() {
        // Given
        when(competitionRepository.findById(1L)).thenReturn(Optional.of(competition));
        when(entryRepository.findByCompetitionIdOrderByDisplayOrder(1L))
                .thenReturn(Arrays.asList(entry));

        // When
        List<CompetitionResponse.EntryResponse> responses = competitionService.getCompetitionEntries(1L);

        // Then
        assertNotNull(responses);
        assertEquals(1, responses.size());
        assertEquals("测试作品", responses.get(0).getEntryName());
    }

    @Test
    @DisplayName("Should update entry status successfully")
    void updateEntryStatus_Success() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));
        when(entryRepository.save(any(CompetitionEntry.class))).thenReturn(entry);

        // When
        competitionService.updateEntryStatus(1L, "APPROVED");

        // Then
        verify(entryRepository).save(any(CompetitionEntry.class));
    }

    @Test
    @DisplayName("Should throw exception for invalid entry status")
    void updateEntryStatus_InvalidStatus() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.updateEntryStatus(1L, "INVALID_STATUS"));
        assertEquals("无效的状态值: INVALID_STATUS", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete entry by contestant successfully")
    void deleteEntry_ByContestant() {
        // Given
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When
        competitionService.deleteEntry(1L, 1L, false);

        // Then
        verify(entryRepository).delete(entry);
    }

    @Test
    @DisplayName("Should delete entry by admin successfully")
    void deleteEntry_ByAdmin() {
        // Given
        entry.setContestant(creator);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When
        competitionService.deleteEntry(1L, 99L, true);

        // Then
        verify(entryRepository).delete(entry);
    }

    @Test
    @DisplayName("Should throw exception when unauthorized user tries to delete entry")
    void deleteEntry_Unauthorized() {
        // Given
        entry.setContestant(creator);
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> competitionService.deleteEntry(1L, 99L, false));
        assertEquals("没有权限删除此作品", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete entry file when deleting entry")
    void deleteEntry_WithFile() {
        // Given
        entry.setFilePath("uploads/test.jpg");
        when(entryRepository.findById(1L)).thenReturn(Optional.of(entry));

        // When
        competitionService.deleteEntry(1L, 1L, false);

        // Then
        verify(fileStorageService).deleteFile("uploads/test.jpg");
        verify(entryRepository).delete(entry);
    }

    // ==================== Scheduled Task Tests ====================

    @Test
    @DisplayName("Should update expired competitions")
    void updateExpiredCompetitions_Success() {
        // Given
        Competition expiredComp = new Competition();
        expiredComp.setId(3L);
        expiredComp.setStatus(Competition.CompetitionStatus.ACTIVE);

        when(competitionRepository.findExpiredActiveCompetitions(any(LocalDateTime.class)))
                .thenReturn(Arrays.asList(expiredComp));
        when(competitionRepository.save(any(Competition.class))).thenReturn(expiredComp);

        // When
        competitionService.updateExpiredCompetitions();

        // Then
        verify(competitionRepository).save(any(Competition.class));
        assertEquals(Competition.CompetitionStatus.ENDED, expiredComp.getStatus());
    }

    @Test
    @DisplayName("Should handle no expired competitions")
    void updateExpiredCompetitions_NoExpired() {
        // Given
        when(competitionRepository.findExpiredActiveCompetitions(any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // When
        competitionService.updateExpiredCompetitions();

        // Then
        verify(competitionRepository, never()).save(any(Competition.class));
    }

    // ==================== Helper Methods ====================

    private CompetitionRequest createValidCompetitionRequest() {
        CompetitionRequest request = new CompetitionRequest();
        request.setName("测试赛事");
        request.setDescription("测试描述");
        request.setModelId(1L);
        request.setDeadline(LocalDateTime.now().plusDays(7));
        return request;
    }

    private EntryRequest createValidEntryRequest() {
        EntryRequest request = new EntryRequest();
        request.setEntryName("测试作品");
        request.setDescription("作品描述");
        return request;
    }
}
