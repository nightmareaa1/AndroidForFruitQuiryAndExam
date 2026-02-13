package com.example.userauth.service;

import com.example.userauth.dto.RatingRequest;
import com.example.userauth.dto.RatingResponse;
import com.example.userauth.entity.*;
import com.example.userauth.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class RatingService {
    
    @Autowired
    private CompetitionRatingRepository ratingRepository;
    
    @Autowired
    private CompetitionRepository competitionRepository;
    
    @Autowired
    private CompetitionEntryRepository entryRepository;
    
    @Autowired
    private CompetitionJudgeRepository judgeRepository;
    
    @Autowired
    private EvaluationParameterRepository parameterRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Submit or update ratings for a competition entry
     */
    public RatingResponse submitRating(RatingRequest request, Long judgeId) {
        // Validate competition exists and is active
        Competition competition = competitionRepository.findById(request.getCompetitionId())
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        // Check if competition can accept ratings (active and not past deadline)
        if (!competition.canAcceptRatings()) {
            if (competition.isDeadlinePassed()) {
                throw new IllegalStateException("赛事已截止，无法提交评分");
            } else {
                throw new IllegalStateException("赛事已结束，无法提交评分");
            }
        }
        
        // Validate entry exists and belongs to the competition
        CompetitionEntry entry = entryRepository.findById(request.getEntryId())
                .orElseThrow(() -> new IllegalArgumentException("参赛作品不存在"));
        
        if (!entry.getCompetition().getId().equals(request.getCompetitionId())) {
            throw new IllegalArgumentException("参赛作品不属于指定赛事");
        }
        
        // Only approved entries can be rated
        if (!entry.isApproved()) {
            throw new IllegalStateException("只能为已审核通过的作品评分");
        }

        User judge = userRepository.findById(judgeId)
                .orElseThrow(() -> new IllegalArgumentException("用户不存在"));

        // Validate judge is authorized for this competition
        // Admin users are automatically granted judge permission
        boolean isAdmin = judge.getIsAdmin() != null && judge.getIsAdmin();
        boolean isJudge = judgeRepository.existsByCompetitionIdAndJudgeId(request.getCompetitionId(), judgeId);

        if (!isAdmin && !isJudge) {
            throw new IllegalArgumentException("您不是该赛事的评委");
        }
        
        // Get all parameters for the competition's evaluation model
        List<EvaluationParameter> modelParameters = parameterRepository
                .findByModelIdOrderByDisplayOrder(competition.getModel().getId());
        
        // Validate that all parameters are included in the rating
        Map<Long, RatingRequest.ScoreRequest> scoreMap = request.getScores().stream()
                .collect(Collectors.toMap(RatingRequest.ScoreRequest::getParameterId, s -> s));
        
        for (EvaluationParameter parameter : modelParameters) {
            if (!scoreMap.containsKey(parameter.getId())) {
                throw new IllegalArgumentException("必须为所有评价参数评分：" + parameter.getName());
            }
        }
        
        // Validate score ranges and save ratings
        List<CompetitionRating> ratings = new ArrayList<>();
        
        for (EvaluationParameter parameter : modelParameters) {
            RatingRequest.ScoreRequest scoreRequest = scoreMap.get(parameter.getId());
            
            // Validate score range (0 to parameter weight)
            BigDecimal weight = new BigDecimal(parameter.getWeight());
            if (scoreRequest.getScore().compareTo(BigDecimal.ZERO) < 0
                    || scoreRequest.getScore().compareTo(weight) > 0) {
                throw new IllegalArgumentException(
                    String.format("参数 %s 的评分必须在 0 到 %d 之间",
                                parameter.getName(), parameter.getWeight()));
            }
            
            // Check if rating already exists for this entry, judge, and parameter
            CompetitionRating existingRating = ratingRepository
                    .findByEntryIdAndJudgeIdAndParameterId(entry.getId(), judgeId, parameter.getId())
                    .orElse(null);
            
            if (existingRating != null) {
                // Update existing rating
                existingRating.setScore(scoreRequest.getScore());
                existingRating.setNote(request.getNote());
                existingRating.setSubmittedAt(LocalDateTime.now());
                ratings.add(existingRating);
            } else {
                // Create new rating
                CompetitionRating rating = new CompetitionRating(
                    competition, entry, judge, parameter, scoreRequest.getScore(), request.getNote());
                ratings.add(rating);
            }
        }
        
        // Save all ratings
        List<CompetitionRating> savedRatings = ratingRepository.saveAll(ratings);
        
        // Convert to response
        return convertToRatingResponse(savedRatings, entry, judge);
    }
    
    /**
     * Get all ratings for a specific competition entry
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByEntry(Long entryId) {
        CompetitionEntry entry = entryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("参赛作品不存在"));
        
        List<CompetitionRating> ratings = ratingRepository.findByEntryIdWithDetails(entryId);
        
        // Group ratings by judge
        Map<Long, List<CompetitionRating>> ratingsByJudge = ratings.stream()
                .collect(Collectors.groupingBy(r -> r.getJudge().getId()));
        
        List<RatingResponse> responses = new ArrayList<>();
        for (Map.Entry<Long, List<CompetitionRating>> judgeRatings : ratingsByJudge.entrySet()) {
            List<CompetitionRating> judgeRatingList = judgeRatings.getValue();
            if (!judgeRatingList.isEmpty()) {
                User judge = judgeRatingList.get(0).getJudge();
                responses.add(convertToRatingResponse(judgeRatingList, entry, judge));
            }
        }
        
        return responses;
    }
    
    /**
     * Get all ratings for a specific competition
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByCompetition(Long competitionId) {
        Competition competition = competitionRepository.findById(competitionId)
                .orElseThrow(() -> new IllegalArgumentException("赛事不存在"));
        
        List<CompetitionRating> ratings = ratingRepository.findByCompetitionIdWithDetails(competitionId);
        
        // Group ratings by entry and judge
        Map<String, List<CompetitionRating>> groupedRatings = ratings.stream()
                .collect(Collectors.groupingBy(r -> r.getEntry().getId() + "_" + r.getJudge().getId()));
        
        List<RatingResponse> responses = new ArrayList<>();
        for (List<CompetitionRating> ratingGroup : groupedRatings.values()) {
            if (!ratingGroup.isEmpty()) {
                CompetitionRating firstRating = ratingGroup.get(0);
                responses.add(convertToRatingResponse(ratingGroup, firstRating.getEntry(), firstRating.getJudge()));
            }
        }
        
        return responses;
    }
    
    /**
     * Get ratings submitted by a specific judge for a competition
     */
    @Transactional(readOnly = true)
    public List<RatingResponse> getRatingsByJudge(Long competitionId, Long judgeId) {
        // Validate judge is authorized for this competition
        if (!judgeRepository.existsByCompetitionIdAndJudgeId(competitionId, judgeId)) {
            throw new IllegalArgumentException("您不是该赛事的评委");
        }
        
        List<CompetitionRating> ratings = ratingRepository.findByCompetitionIdAndJudgeId(competitionId, judgeId);
        
        // Group ratings by entry
        Map<Long, List<CompetitionRating>> ratingsByEntry = ratings.stream()
                .collect(Collectors.groupingBy(r -> r.getEntry().getId()));
        
        List<RatingResponse> responses = new ArrayList<>();
        for (List<CompetitionRating> entryRatings : ratingsByEntry.values()) {
            if (!entryRatings.isEmpty()) {
                CompetitionRating firstRating = entryRatings.get(0);
                responses.add(convertToRatingResponse(entryRatings, firstRating.getEntry(), firstRating.getJudge()));
            }
        }
        
        return responses;
    }
    
    /**
     * Check if a judge has completed rating for an entry
     */
    @Transactional(readOnly = true)
    public boolean hasJudgeCompletedRating(Long entryId, Long judgeId) {
        CompetitionEntry entry = entryRepository.findByIdWithCompetition(entryId)
                .orElseThrow(() -> new IllegalArgumentException("参赛作品不存在"));
        
        // Get the number of parameters in the evaluation model
        int parameterCount = parameterRepository.findByModelIdOrderByDisplayOrder(
                entry.getCompetition().getModel().getId()).size();
        
        // Count how many parameters the judge has rated for this entry
        long ratedParameterCount = ratingRepository.countByEntryIdAndJudgeId(entryId, judgeId);
        
        return ratedParameterCount >= parameterCount;
    }
    
    /**
     * Delete all ratings for a competition (used when competition is deleted)
     */
    public void deleteRatingsByCompetition(Long competitionId) {
        List<CompetitionRating> ratings = ratingRepository.findByCompetitionIdWithDetails(competitionId);
        ratingRepository.deleteAll(ratings);
    }
    
    /**
     * Convert CompetitionRating entities to RatingResponse DTO
     */
    private RatingResponse convertToRatingResponse(List<CompetitionRating> ratings, 
                                                  CompetitionEntry entry, User judge) {
        if (ratings.isEmpty()) {
            return null;
        }
        
        CompetitionRating firstRating = ratings.get(0);
        
        List<RatingResponse.ScoreResponse> scores = ratings.stream()
                .map(rating -> new RatingResponse.ScoreResponse(
                    rating.getParameter().getId(),
                    rating.getParameter().getName(),
                    rating.getParameter().getWeight(),
                    rating.getScore()
                ))
                .collect(Collectors.toList());
        
        return new RatingResponse(
            firstRating.getId(),
            firstRating.getCompetition().getId(),
            entry.getId(),
            entry.getEntryName(),
            judge.getId(),
            judge.getUsername(),
            scores,
            firstRating.getNote(),
            firstRating.getSubmittedAt()
        );
    }
}