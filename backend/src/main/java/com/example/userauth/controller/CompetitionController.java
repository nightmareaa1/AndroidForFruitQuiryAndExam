package com.example.userauth.controller;

import com.example.userauth.dto.CompetitionRequest;
import com.example.userauth.dto.CompetitionResponse;
import com.example.userauth.dto.EntryRequest;
import com.example.userauth.dto.EntryStatusUpdateRequest;
import com.example.userauth.dto.EntrySubmitResponse;
import com.example.userauth.entity.User;
import com.example.userauth.repository.UserRepository;
import com.example.userauth.security.RequireAdmin;
import com.example.userauth.service.CompetitionService;
import com.example.userauth.service.RatingDataService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for competition management
 */
@RestController
@RequestMapping("/api/competitions")
@CrossOrigin(origins = "*", maxAge = 3600)
public class CompetitionController {
    
    private static final Logger logger = LoggerFactory.getLogger(CompetitionController.class);
    
    @Autowired
    private CompetitionService competitionService;
    
    @Autowired
    private RatingDataService ratingDataService;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all competitions
     * For admin: returns all competitions
     * For regular users: returns competitions where user is creator or judge
     */
    @GetMapping
    public ResponseEntity<List<CompetitionResponse>> getAllCompetitions() {
        logger.info("GET /api/competitions - Fetching competitions");
        
        try {
            User currentUser = getCurrentUser();
            List<CompetitionResponse> competitions = competitionService.getAllCompetitions(
                    currentUser.getId(), 
                    currentUser.isAdmin()
            );
            
            logger.info("Successfully retrieved {} competitions for user: {}", 
                       competitions.size(), currentUser.getUsername());
            return ResponseEntity.ok(competitions);
        } catch (Exception e) {
            logger.error("Error fetching competitions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get competitions created by current user
     */
    @GetMapping("/created")
    public ResponseEntity<List<CompetitionResponse>> getCreatedCompetitions() {
        logger.info("GET /api/competitions/created - Fetching competitions created by current user");
        
        try {
            User currentUser = getCurrentUser();
            List<CompetitionResponse> competitions = competitionService.getCompetitionsByCreator(currentUser.getId());
            
            logger.info("Successfully retrieved {} created competitions for user: {}", 
                       competitions.size(), currentUser.getUsername());
            return ResponseEntity.ok(competitions);
        } catch (Exception e) {
            logger.error("Error fetching created competitions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get competitions where current user is a judge
     */
    @GetMapping("/judged")
    public ResponseEntity<List<CompetitionResponse>> getJudgedCompetitions() {
        logger.info("GET /api/competitions/judged - Fetching competitions where current user is a judge");
        
        try {
            User currentUser = getCurrentUser();
            List<CompetitionResponse> competitions = competitionService.getCompetitionsByJudge(currentUser.getId());
            
            logger.info("Successfully retrieved {} judged competitions for user: {}", 
                       competitions.size(), currentUser.getUsername());
            return ResponseEntity.ok(competitions);
        } catch (Exception e) {
            logger.error("Error fetching judged competitions", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get competition by id
     */
    @GetMapping("/{id}")
    public ResponseEntity<CompetitionResponse> getCompetitionById(@PathVariable Long id) {
        logger.info("GET /api/competitions/{} - Fetching competition", id);
        
        try {
            Optional<CompetitionResponse> competition = competitionService.getCompetitionById(id);
            if (competition.isPresent()) {
                logger.info("Successfully retrieved competition with id: {}", id);
                return ResponseEntity.ok(competition.get());
            } else {
                logger.warn("Competition not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new competition
     * Requires admin privileges
     */
    @PostMapping
    @RequireAdmin(message = "只有管理员可以创建赛事")
    public ResponseEntity<CompetitionResponse> createCompetition(@Valid @RequestBody CompetitionRequest request) {
        logger.info("POST /api/competitions - Creating new competition: {}", request.getName());
        
        try {
            User currentUser = getCurrentUser();
            CompetitionResponse response = competitionService.createCompetition(request, currentUser.getId());
            
            logger.info("Successfully created competition with id: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for creating competition: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating competition", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update competition
     * Only competition creator can update
     */
    @PutMapping("/{id}")
    public ResponseEntity<CompetitionResponse> updateCompetition(@PathVariable Long id, 
                                                               @Valid @RequestBody CompetitionRequest request) {
        logger.info("PUT /api/competitions/{} - Updating competition", id);
        
        try {
            User currentUser = getCurrentUser();
            CompetitionResponse response = competitionService.updateCompetition(id, request, currentUser.getId());
            
            logger.info("Successfully updated competition with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating competition {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete competition
     * Only competition creator can delete
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCompetition(@PathVariable Long id) {
        logger.info("DELETE /api/competitions/{} - Deleting competition", id);
        
        try {
            User currentUser = getCurrentUser();
            competitionService.deleteCompetition(id, currentUser.getId());
            
            logger.info("Successfully deleted competition with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Competition not found for deletion or access denied: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error deleting competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Add judges to competition
     * Only competition creator can add judges
     */
    @PostMapping("/{id}/judges")
    public ResponseEntity<Void> addJudges(@PathVariable Long id, @RequestBody List<Long> judgeIds) {
        logger.info("POST /api/competitions/{}/judges - Adding judges to competition", id);
        
        try {
            competitionService.addJudgesToCompetition(id, judgeIds);
            
            logger.info("Successfully added {} judges to competition {}", judgeIds.size(), id);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for adding judges to competition {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error adding judges to competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Remove judge from competition
     * Only competition creator can remove judges
     */
    @DeleteMapping("/{id}/judges/{judgeId}")
    public ResponseEntity<Void> removeJudge(@PathVariable Long id, @PathVariable Long judgeId) {
        logger.info("DELETE /api/competitions/{}/judges/{} - Removing judge from competition", id, judgeId);
        
        try {
            User currentUser = getCurrentUser();
            competitionService.removeJudgeFromCompetition(id, judgeId, currentUser.getId());
            
            logger.info("Successfully removed judge {} from competition {}", judgeId, id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for removing judge from competition: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error removing judge {} from competition {}", judgeId, id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Add entries to competition
     * Requires admin privileges (only admin can add entries for now)
     */
    @PostMapping("/{id}/entries")
    @RequireAdmin(message = "只有管理员可以添加参赛作品")
    public ResponseEntity<List<Long>> addEntries(@PathVariable Long id,
                                               @RequestPart("entries") List<EntryRequest> entryRequests,
                                               @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        logger.info("POST /api/competitions/{}/entries - Adding {} entries to competition", id, entryRequests.size());
        
        try {
            User currentUser = getCurrentUser();
            List<Long> entryIds = competitionService.addEntriesToCompetition(id, entryRequests, files, currentUser.getId());
            
            logger.info("Successfully added {} entries to competition {}", entryIds.size(), id);
            return ResponseEntity.status(HttpStatus.CREATED).body(entryIds);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for adding entries to competition {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error adding entries to competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Submit entry to competition with optional file upload
     * Any authenticated user can submit their entry
     */
    @PostMapping("/{id}/submit")
    public ResponseEntity<?> submitEntry(@PathVariable Long id,
                                        @RequestPart("entry") @Valid EntryRequest request,
                                        @RequestPart(value = "file", required = false) MultipartFile file) {
        logger.info("POST /api/competitions/{}/submit - User submitting entry: {}", id, request.getEntryName());
        
        try {
            User currentUser = getCurrentUser();
            Long entryId = competitionService.submitEntryToCompetition(id, request, file, currentUser.getId());
            
            logger.info("Successfully submitted entry {} to competition {}", entryId, id);
            return ResponseEntity.status(HttpStatus.CREATED).body(new EntrySubmitResponse(entryId, "参赛作品提交成功"));
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for submitting entry to competition {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Error submitting entry to competition with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("提交失败，请稍后重试"));
        }
    }
    
    /**
     * Get all entries for a competition (for review)
     * Requires admin privileges
     */
    @GetMapping("/{id}/entries")
    @RequireAdmin(message = "只有管理员可以查看参赛作品列表")
    public ResponseEntity<List<CompetitionResponse.EntryResponse>> getCompetitionEntries(@PathVariable Long id) {
        logger.info("GET /api/competitions/{}/entries - Fetching entries for competition", id);
        
        try {
            List<CompetitionResponse.EntryResponse> entries = competitionService.getCompetitionEntries(id);
            logger.info("Successfully retrieved {} entries for competition {}", entries.size(), id);
            return ResponseEntity.ok(entries);
        } catch (IllegalArgumentException e) {
            logger.warn("Competition not found: {}", id);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            logger.error("Error fetching entries for competition {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update entry status (approve/reject)
     * Requires admin privileges
     */
    @PutMapping("/{competitionId}/entries/{entryId}/status")
    @RequireAdmin(message = "只有管理员可以审核作品")
    public ResponseEntity<Void> updateEntryStatus(
            @PathVariable Long competitionId,
            @PathVariable Long entryId,
            @RequestBody EntryStatusUpdateRequest request) {
        logger.info("PUT /api/competitions/{}/entries/{}/status - Updating entry status to {}", 
                   competitionId, entryId, request.getStatus());
        
        try {
            competitionService.updateEntryStatus(entryId, request.getStatus());
            logger.info("Successfully updated entry {} status to {}", entryId, request.getStatus());
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating entry status: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating entry {} status", entryId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Export competition rating data as CSV
     * Only competition creator (admin) can export
     * Endpoint: GET /api/competitions/{id}/export
     */
    @GetMapping("/{id}/export")
    public ResponseEntity<?> exportCompetitionData(@PathVariable Long id) {
        logger.info("GET /api/competitions/{}/export - Exporting competition rating data as CSV", id);
        
        try {
            User currentUser = getCurrentUser();
            
            // Check if user has permission to export (admin only)
            if (!ratingDataService.canExportRatingData(id, currentUser.getId())) {
                logger.warn("User {} does not have permission to export data for competition {}", 
                           currentUser.getUsername(), id);
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(createErrorResponse("只有管理员可以导出评分数据"));
            }
            
            String csvData = ratingDataService.generateCompetitionCSV(id);
            
            // Set response headers for CSV download
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv; charset=UTF-8"));
            headers.setContentDispositionFormData("attachment", "competition_" + id + "_ratings.csv");
            
            logger.info("Successfully generated CSV export for competition {} by user {}", 
                       id, currentUser.getUsername());
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(csvData);
            
        } catch (IllegalArgumentException e) {
            logger.warn("Competition not found for export: {}", e.getMessage());
            return ResponseEntity.notFound().build();
            
        } catch (Exception e) {
            logger.error("Error exporting competition data for competition {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("导出数据失败，请稍后重试"));
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
}