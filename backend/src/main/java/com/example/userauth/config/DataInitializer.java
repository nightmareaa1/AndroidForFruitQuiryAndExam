package com.example.userauth.config;

import com.example.userauth.service.EvaluationModelService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component to initialize application data on startup
 */
@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private EvaluationModelService evaluationModelService;
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting data initialization...");
        
        try {
            // Initialize preset evaluation models
            evaluationModelService.initializePresetModels();
            
            logger.info("Data initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during data initialization", e);
            // Don't throw exception to prevent application startup failure
        }
    }
}