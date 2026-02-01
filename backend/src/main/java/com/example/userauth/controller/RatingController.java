package com.example.userauth.controller;

import com.example.userauth.dto.CompetitionRatingDataResponse;
import com.example.userauth.dto.RatingRequest;
import com.example.userauth.dto.RatingResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.service.RatingDataService;
import com.example.userauth.service.RatingService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for rating management
 */
@RestController
@RequestMapping("/api/ratings")
@CrossOrigin(origins = "*", maxAge = 3600)
public class RatingController {
    
    private static final Logger logger = LoggerFactory.getLogger(RatingController.class);
    
    @Autowired
    private RatingService ratingService;
    
    @Autowired
    private RatingDataService ratingDataService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Submit or update ratings for a competition entry
     * Only judges can submit ratings
     */
    @PostMapping
    public ResponseEntity<?> submitRating(@Valid @RequestBody RatingRequest request) {
        logger.info("POST /api/ratings - Submitting rating for competition {} entry {}", 
                   request.getCompetitionId(), request.getEntryId());
        
        try {
            User currentUser = getCurrentUser();
            RatingResponse response = ratingService.submitRating(request, currentUser.getId());
            
            logger.info("Successfully submitted rating for competition {} entry {} by judge {}", 
                       request.getCompetitionId(), request.getEntryId(), currentUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid rating request: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
            
        } catch (IllegalStateException e) {
            logger.warn("Rating submission not allowed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
            
        } catch (Exception e) {
            logger.error("Error submitting rating for competition {} entry {}", 
                        request.getCompetitionId(), request.getEntryId(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("评分提交失败，请稍后重试"));
        }
    }
    
    /**
     * Get all ratings for a specific competition entry
     * Accessible to competition creator, judges, and admin
     */
    @GetMapping("/entry/{entryId}")
    public ResponseEntity<?> getRatingsByEntry(@PathVariable Long entryId) {
        logger.info("GET /api/ratings/entry/{} - Fetching ratings for entry", entryId);
        
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByEntry(entryId);
            
            logger.info("Successfully retrieved {} ratings for entry {}", ratings.size(), entryId);
            return ResponseEntity.ok(ratings);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Entry not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error fetching ratings for entry {}", entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("获取评分数据失败，请稍后重试"));
        }
    }
    
    /**
     * Get all ratings for a specific competition
     * Accessible to competition creator, judges, and admin
     */
    @GetMapping("/competition/{competitionId}")
    public ResponseEntity<?> getRatingsByCompetition(@PathVariable Long competitionId) {
        logger.info("GET /api/ratings/competition/{} - Fetching ratings for competition", competitionId);
        
        try {
            List<RatingResponse> ratings = ratingService.getRatingsByCompetition(competitionId);
            
            logger.info("Successfully retrieved {} ratings for competition {}", ratings.size(), competitionId);
            return ResponseEntity.ok(ratings);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Competition not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error fetching ratings for competition {}", competitionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("获取评分数据失败，请稍后重试"));
        }
    }
    
    /**
     * Get ratings submitted by current user (judge) for a specific competition
     * Only accessible to the judge themselves
     */
    @GetMapping("/competition/{competitionId}/my-ratings")
    public ResponseEntity<?> getMyRatings(@PathVariable Long competitionId) {
        logger.info("GET /api/ratings/competition/{}/my-ratings - Fetching current user's ratings", competitionId);
        
        try {
            User currentUser = getCurrentUser();
            List<RatingResponse> ratings = ratingService.getRatingsByJudge(competitionId, currentUser.getId());
            
            logger.info("Successfully retrieved {} ratings for competition {} by judge {}", 
                       ratings.size(), competitionId, currentUser.getUsername());
            return ResponseEntity.ok(ratings);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for getting judge ratings: {}", e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
            
        } catch (Exception e) {
            logger.error("Error fetching ratings for competition {} by current user", competitionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("获取评分数据失败，请稍后重试"));
        }
    }
    
    /**
     * Check if current user (judge) has completed rating for a specific entry
     */
    @GetMapping("/entry/{entryId}/completion-status")
    public ResponseEntity<?> getRatingCompletionStatus(@PathVariable Long entryId) {
        logger.info("GET /api/ratings/entry/{}/completion-status - Checking rating completion status", entryId);
        
        try {
            User currentUser = getCurrentUser();
            boolean isCompleted = ratingService.hasJudgeCompletedRating(entryId, currentUser.getId());
            
            logger.info("Rating completion status for entry {} by judge {}: {}", 
                       entryId, currentUser.getUsername(), isCompleted);
            return ResponseEntity.ok(new RatingCompletionResponse(isCompleted));
            
        } catch (IllegalArgumentException e) {
            logger.warn("Entry not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error checking rating completion status for entry {}", entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("检查评分状态失败，请稍后重试"));
        }
    }
    
    /**
     * Get aggregated rating data for a competition (with averages and statistics)
     * Accessible to competition creator, judges, and all users if competition has ended
     * Endpoint: GET /api/ratings/{competitionId}
     */
    @GetMapping("/{competitionId}")
    public ResponseEntity<?> getCompetitionRatingData(@PathVariable Long competitionId) {
        logger.info("GET /api/ratings/{} - Fetching aggregated rating data for competition", competitionId);
        
        try {
            User currentUser = getCurrentUser();
            
            // Check if user has permission to view rating data
            if (!ratingDataService.canViewRatingData(competitionId, currentUser.getId())) {
                logger.warn("User {} does not have permission to view rating data for competition {}", 
                           currentUser.getUsername(), competitionId);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("您没有权限查看该赛事的评分数据"));
            }
            
            CompetitionRatingDataResponse ratingData = ratingDataService.getCompetitionRatingData(competitionId);
            
            logger.info("Successfully retrieved aggregated rating data for competition {} with {} entries", 
                       competitionId, ratingData.getEntries().size());
            return ResponseEntity.ok(ratingData);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Competition not found: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error fetching aggregated rating data for competition {}", competitionId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("获取评分数据失败，请稍后重试"));
        }
    }
    
    /**
     * Get current authenticated user
     */
    private User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Current user not found: " + username));
    }
    
    /**
     * Create error response object
     */
    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message);
    }
    
    /**
     * Error response DTO
     */
    public static class ErrorResponse {
        private String message;
        
        public ErrorResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
    
    /**
     * Rating completion response DTO
     */
    public static class RatingCompletionResponse {
        private boolean completed;
        
        public RatingCompletionResponse(boolean completed) {
            this.completed = completed;
        }
        
        public boolean isCompleted() {
            return completed;
        }
        
        public void setCompleted(boolean completed) {
            this.completed = completed;
        }
    }
}