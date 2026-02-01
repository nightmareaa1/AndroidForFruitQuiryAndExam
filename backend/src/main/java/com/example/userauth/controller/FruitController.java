package com.example.userauth.controller;

import com.example.userauth.dto.FruitQueryResponse;
import com.example.userauth.service.FruitQueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for fruit nutrition and flavor queries.
 * Provides endpoints for querying fruit data by type and name.
 */
@RestController
@RequestMapping("/api/fruit")
@CrossOrigin(origins = "*", maxAge = 3600)
public class FruitController {

    private static final Logger logger = LoggerFactory.getLogger(FruitController.class);

    private final FruitQueryService fruitQueryService;

    @Autowired
    public FruitController(FruitQueryService fruitQueryService) {
        this.fruitQueryService = fruitQueryService;
    }

    /**
     * Query fruit data by type and fruit name.
     * GET /api/fruit/query?type={nutrition|flavor}&fruit={fruitName}
     * 
     * @param type the query type ("nutrition" or "flavor")
     * @param fruit the fruit name
     * @return ResponseEntity containing FruitQueryResponse or error
     */
    @GetMapping("/query")
    public ResponseEntity<FruitQueryResponse> queryFruitData(
            @RequestParam(name = "type", required = false) String type,
            @RequestParam(name = "fruit", required = false) String fruit) {
        
        logger.info("GET /api/fruit/query - type={}, fruit={}", type, fruit);

        try {
            // Validate required parameters
            if (type == null || type.trim().isEmpty()) {
                logger.warn("Missing required parameter: type");
                return ResponseEntity.badRequest().build();
            }
            
            if (fruit == null || fruit.trim().isEmpty()) {
                logger.warn("Missing required parameter: fruit");
                return ResponseEntity.badRequest().build();
            }

            // Validate query type
            if (!"nutrition".equalsIgnoreCase(type) && !"flavor".equalsIgnoreCase(type)) {
                logger.warn("Invalid query type: {}. Must be 'nutrition' or 'flavor'", type);
                return ResponseEntity.badRequest().build();
            }

            // Query fruit data
            FruitQueryResponse response = fruitQueryService.queryFruitData(type, fruit);
            
            logger.info("Successfully queried fruit data: type={}, fruit={}, dataCount={}", 
                       type, fruit, response.getData().size());
            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {
            logger.warn("Invalid request parameters: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
            
        } catch (RuntimeException e) {
            // Handle fruit not found
            if (e.getMessage() != null && e.getMessage().contains("not found")) {
                logger.warn("Fruit not found: {}", fruit);
                return ResponseEntity.notFound().build();
            }
            
            // Handle other runtime exceptions
            logger.error("Error querying fruit data: type={}, fruit={}", type, fruit, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            
        } catch (Exception e) {
            logger.error("Unexpected error querying fruit data: type={}, fruit={}", type, fruit, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}