package com.example.userauth.service;

import com.example.userauth.dto.CompetitionRequest;
import com.example.userauth.dto.CompetitionResponse;
import com.example.userauth.dto.EntryRequest;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CompetitionService {
    
    private static final Logger logger = LoggerFactory.getLogger(CompetitionService.class);
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private CompetitionJudgeRepository judgeRepository;
    
    @Autowired
    private CompetitionEntryRepository entryRepository;
    
    @Autowired
    private EvaluationModelRepository modelRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private FileStorageService fileStorageService;
    
    /**
     * Get all competitions (for admin) or competitions where user is creator/judge
     */
    @Transactional(readOnly = true)
    public List<CompetitionResponse> getAllCompetitions(Long userId, boolean isAdmin) {
        logger.info("Fetching competitions for user: {}, isAdmin: {}", userId, isAdmin);
        
        List<Competition> competitions;
        if (isAdmin) {
            competitions = competitionRepository.findAll();
        } else {
            // Get competitions where user is creator or judge
            List<Competition> createdCompetitions = competitionRepository.findByCreatorIdOrderByCreatedAtDesc(userId);
            List<Competition> judgedCompetitions = competitionRepository.findByJudgeIdOrderByCreatedAtDesc(userId);
            
            // Combine and deduplicate
            competitions = createdCompetitions;
            judgedCompetitions.stream()
                    .filter(c -> !createdCompetitions.contains(c))
                    .forEach(competitions::add);
        }
        
        return competitions.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Get competitions created by a specific user
     */
    @Transactional(readOnly = true)
    public List<CompetitionResponse> getCompetitionsByCreator(Long creatorId) {
        logger.info("Fetching competitions created by user: {}", creatorId);
        List<Competition> competitions = competitionRepository.findByCreatorIdOrderByCreatedAtDesc(creatorId);
        return competitions.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Get competitions where user is a judge
     */
    @Transactional(readOnly = true)
    public List<CompetitionResponse> getCompetitionsByJudge(Long judgeId) {
        logger.info("Fetching competitions where user {} is a judge", judgeId);
        List<Competition> competitions = competitionRepository.findByJudgeIdOrderByCreatedAtDesc(judgeId);
        return competitions.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Get competition by id
     */
    @Transactional(readOnly = true)
    public Optional<CompetitionResponse> getCompetitionById(Long id) {
        logger.info("Fetching competition with id: {}", id);
        Optional<Competition> competition = competitionRepository.findByIdWithDetails(id);
        return competition.map(this::convertToResponse);
    }
    
    /**
     * Create new competition
     */
    public CompetitionResponse createCompetition(CompetitionRequest request, Long creatorId) {
        logger.info("Creating new competition: {} by user: {}", request.getName(), creatorId);
        
        // Validate evaluation model exists
        EvaluationModel model = modelRepository.findById(request.getModelId())
                .orElseThrow(() -> new IllegalArgumentException("评价模型不存在: " + request.getModelId()));
        
        // Validate creator exists
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + creatorId));
        
        // Validate deadline is in the future
        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("截止时间必须在未来");
        }
        
        // Create competition
        Competition competition = new Competition(
                request.getName(),
                request.getDescription(),
                model,
                creator,
                request.getDeadline()
        );
        
        competition = competitionRepository.save(competition);
        
        // Add judges if provided
        if (request.getJudgeIds() != null && !request.getJudgeIds().isEmpty()) {
            addJudgesToCompetition(competition.getId(), request.getJudgeIds());
        }
        
        logger.info("Successfully created competition with id: {}", competition.getId());
        return convertToResponse(competition);
    }
    
    /**
     * Update competition
     */
    public CompetitionResponse updateCompetition(Long id, CompetitionRequest request, Long userId) {
        logger.info("Updating competition with id: {} by user: {}", id, userId);
        
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + id));
        
        // Check if user is the creator
        if (!competition.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("只有赛事创建者可以修改赛事");
        }
        
        // Validate evaluation model exists if changed
        if (!competition.getModel().getId().equals(request.getModelId())) {
            EvaluationModel model = modelRepository.findById(request.getModelId())
                    .orElseThrow(() -> new IllegalArgumentException("评价模型不存在: " + request.getModelId()));
            competition.setModel(model);
        }
        
        // Validate deadline is in the future
        if (request.getDeadline().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("截止时间必须在未来");
        }
        
        // Update competition fields
        competition.setName(request.getName());
        competition.setDescription(request.getDescription());
        competition.setDeadline(request.getDeadline());
        
        competition = competitionRepository.save(competition);
        
        logger.info("Successfully updated competition with id: {}", id);
        return convertToResponse(competition);
    }
    
    /**
     * Delete competition
     */
    public void deleteCompetition(Long id, Long userId) {
        logger.info("Deleting competition with id: {} by user: {}", id, userId);
        
        Competition competition = competitionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + id));
        
        // Check if user is the creator
        if (!competition.getCreator().getId().equals(userId)) {
            throw new IllegalArgumentException("只有赛事创建者可以删除赛事");
        }
        
        competitionRepository.deleteById(id);
        logger.info("Successfully deleted competition with id: {}", id);
    }
    
    /**
     * Add judges to competition
     */
    public void addJudgesToCompetition(Long competitionId, List<Long> judgeIds) {
        logger.info("Adding judges to competition: {}", competitionId);
        
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + competitionId));
        
        for (Long judgeId : judgeIds) {
            // Check if user exists
            User judge = userRepository.findById(judgeId)
                    .orElseThrow(() -> new IllegalArgumentException("用户不存在: " + judgeId));
            
            // Check if user is already a judge
            if (!judgeRepository.existsByCompetitionIdAndJudgeId(competitionId, judgeId)) {
                CompetitionJudge competitionJudge = new CompetitionJudge(competition, judge);
                judgeRepository.save(competitionJudge);
                logger.info("Added judge {} to competition {}", judgeId, competitionId);
            } else {
                logger.warn("User {} is already a judge for competition {}", judgeId, competitionId);
            }
        }
    }
    
    /**
     * Remove judge from competition
     */
    public void removeJudgeFromCompetition(Long competitionId, Long judgeId, Long requesterId) {
        logger.info("Removing judge {} from competition {} by user {}", judgeId, competitionId, requesterId);
        
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + competitionId));
        
        // Check if requester is the creator
        if (!competition.getCreator().getId().equals(requesterId)) {
            throw new IllegalArgumentException("只有赛事创建者可以移除评委");
        }
        
        judgeRepository.deleteByCompetitionIdAndJudgeId(competitionId, judgeId);
        logger.info("Successfully removed judge {} from competition {}", judgeId, competitionId);
    }
    
    /**
     * Add entries to competition
     */
    public List<Long> addEntriesToCompetition(Long competitionId, List<EntryRequest> entryRequests, 
                                            List<MultipartFile> files, Long requesterId) {
        logger.info("Adding {} entries to competition {} by user {}", entryRequests.size(), competitionId, requesterId);
        
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在: " + competitionId));
        
        // Check if requester is the creator (admin only for now)
        if (!competition.getCreator().getId().equals(requesterId)) {
            throw new IllegalArgumentException("只有赛事创建者可以添加参赛作品");
        }
        
        // Check if deadline has passed
        if (competition.isDeadlinePassed()) {
            throw new IllegalArgumentException("赛事截止时间已过，无法添加参赛作品");
        }
        
        // Get next display order
        Integer maxDisplayOrder = entryRepository.findMaxDisplayOrderByCompetitionId(competitionId);
        int nextDisplayOrder = (maxDisplayOrder != null ? maxDisplayOrder : 0) + 1;
        
        List<Long> entryIds = new java.util.ArrayList<>();
        
        for (int i = 0; i < entryRequests.size(); i++) {
            EntryRequest entryRequest = entryRequests.get(i);
            MultipartFile file = (files != null && i < files.size()) ? files.get(i) : null;
            
            String filePath = null;
            if (file != null && !file.isEmpty()) {
                try {
                    filePath = fileStorageService.storeFile(file);
                } catch (java.io.IOException e) {
                    logger.error("Failed to store file for entry: {}", entryRequest.getEntryName(), e);
                    throw new RuntimeException("文件上传失败: " + e.getMessage(), e);
                }
            }
            
            CompetitionEntry entry = new CompetitionEntry(
                    competition,
                    entryRequest.getEntryName(),
                    entryRequest.getDescription(),
                    filePath,
                    nextDisplayOrder + i
            );
            
            entry = entryRepository.save(entry);
            entryIds.add(entry.getId());
            
            logger.info("Added entry {} to competition {}", entry.getId(), competitionId);
        }
        
        return entryIds;
    }
    
    /**
     * Update competition status automatically based on deadline
     * Scheduled to run every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 milliseconds
    public void updateExpiredCompetitions() {
        logger.info("Checking for expired competitions");
        
        List<Competition> expiredCompetitions = competitionRepository.findExpiredActiveCompetitions(LocalDateTime.now());
        
        for (Competition competition : expiredCompetitions) {
            competition.setStatus(Competition.CompetitionStatus.ENDED);
            competitionRepository.save(competition);
            logger.info("Updated competition {} status to ENDED", competition.getId());
        }
        
        if (!expiredCompetitions.isEmpty()) {
            logger.info("Updated {} expired competitions", expiredCompetitions.size());
        }
    }
    
    /**
     * Convert entity to response DTO
     */
    private CompetitionResponse convertToResponse(Competition competition) {
        List<CompetitionResponse.JudgeResponse> judgeResponses = competition.getJudges() != null ?
                competition.getJudges().stream()
                        .map(j -> new CompetitionResponse.JudgeResponse(
                                j.getId(),
                                j.getJudge().getId(),
                                j.getJudge().getUsername(),
                                j.getCreatedAt()
                        ))
                        .toList() :
                List.of();
        
        List<CompetitionResponse.EntryResponse> entryResponses = competition.getEntries() != null ?
                competition.getEntries().stream()
                        .map(e -> new CompetitionResponse.EntryResponse(
                                e.getId(),
                                e.getEntryName(),
                                e.getDescription(),
                                e.getFilePath(),
                                e.getDisplayOrder(),
                                e.getStatus().name(),
                                e.getCreatedAt(),
                                e.getUpdatedAt()
                        ))
                        .toList() :
                List.of();
        
        return new CompetitionResponse(
                competition.getId(),
                competition.getName(),
                competition.getDescription(),
                competition.getModel().getId(),
                competition.getModel().getName(),
                competition.getCreator().getId(),
                competition.getCreator().getUsername(),
                competition.getDeadline(),
                competition.getStatus().name(),
                judgeResponses,
                entryResponses,
                competition.getCreatedAt(),
                competition.getUpdatedAt()
        );
    }
}