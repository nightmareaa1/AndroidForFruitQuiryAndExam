package com.example.userauth.controller;

import com.example.userauth.dto.ModelRequest;
import com.example.userauth.dto.ModelResponse;
import com.example.userauth.security.RequireAdmin;
import com.example.userauth.service.EvaluationModelService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * REST Controller for evaluation model management
 */
@RestController
@RequestMapping("/api/evaluation-models")
@CrossOrigin(origins = "*", maxAge = 3600)
public class EvaluationModelController {
    
    private static final Logger logger = LoggerFactory.getLogger(EvaluationModelController.class);
    
    @Autowired
    private EvaluationModelService evaluationModelService;
    
    /**
     * Get all evaluation models
     * Accessible to all authenticated users
     */
    @GetMapping
    public ResponseEntity<List<ModelResponse>> getAllModels() {
        logger.info("GET /api/evaluation-models - Fetching all evaluation models");
        
        try {
            List<ModelResponse> models = evaluationModelService.getAllModels();
            logger.info("Successfully retrieved {} evaluation models", models.size());
            return ResponseEntity.ok(models);
        } catch (Exception e) {
            logger.error("Error fetching evaluation models", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Get evaluation model by id
     * Accessible to all authenticated users
     */
    @GetMapping("/{id}")
    public ResponseEntity<ModelResponse> getModelById(@PathVariable Long id) {
        logger.info("GET /api/evaluation-models/{} - Fetching evaluation model", id);
        
        try {
            Optional<ModelResponse> model = evaluationModelService.getModelById(id);
            if (model.isPresent()) {
                logger.info("Successfully retrieved evaluation model with id: {}", id);
                return ResponseEntity.ok(model.get());
            } else {
                logger.warn("Evaluation model not found with id: {}", id);
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            logger.error("Error fetching evaluation model with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Create new evaluation model
     * Requires admin privileges
     */
    @PostMapping
    @RequireAdmin(message = "只有管理员可以创建评价模型")
    public ResponseEntity<ModelResponse> createModel(@Valid @RequestBody ModelRequest request) {
        logger.info("POST /api/evaluation-models - Creating new evaluation model: {}", request.getName());
        
        try {
            ModelResponse response = evaluationModelService.createModel(request);
            logger.info("Successfully created evaluation model with id: {}", response.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for creating evaluation model: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error creating evaluation model", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Update evaluation model
     * Requires admin privileges
     */
    @PutMapping("/{id}")
    @RequireAdmin(message = "只有管理员可以更新评价模型")
    public ResponseEntity<ModelResponse> updateModel(@PathVariable Long id, 
                                                   @Valid @RequestBody ModelRequest request) {
        logger.info("PUT /api/evaluation-models/{} - Updating evaluation model", id);
        
        try {
            ModelResponse response = evaluationModelService.updateModel(id, request);
            logger.info("Successfully updated evaluation model with id: {}", id);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request for updating evaluation model {}: {}", id, e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            logger.error("Error updating evaluation model with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    
    /**
     * Delete evaluation model
     * Requires admin privileges
     */
    @DeleteMapping("/{id}")
    @RequireAdmin(message = "只有管理员可以删除评价模型")
    public ResponseEntity<Void> deleteModel(@PathVariable Long id) {
        logger.info("DELETE /api/evaluation-models/{} - Deleting evaluation model", id);
        
        try {
            evaluationModelService.deleteModel(id);
            logger.info("Successfully deleted evaluation model with id: {}", id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            logger.warn("Evaluation model not found for deletion: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            logger.warn("Cannot delete evaluation model: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } catch (Exception e) {
            logger.error("Error deleting evaluation model with id: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}