package com.example.userauth.service;

import com.example.userauth.dto.ModelRequest;
import com.example.userauth.dto.ModelResponse;
import com.example.userauth.entity.EvaluationModel;
import com.example.userauth.entity.EvaluationParameter;
import com.example.userauth.repository.EvaluationModelRepository;
import com.example.userauth.repository.EvaluationParameterRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.IntStream;

@Service
@Transactional
public class EvaluationModelService {
    
    private static final Logger logger = LoggerFactory.getLogger(EvaluationModelService.class);
    
    @Autowired
    private EvaluationModelRepository modelRepository;
    
    @Autowired
    private EvaluationParameterRepository parameterRepository;
    
    /**
     * Get all evaluation models
     */
    @Transactional(readOnly = true)
    public List<ModelResponse> getAllModels() {
        logger.info("Fetching all evaluation models");
        List<EvaluationModel> models = modelRepository.findAllWithParameters();
        return models.stream()
                .map(this::convertToResponse)
                .toList();
    }
    
    /**
     * Get evaluation model by id
     */
    @Transactional(readOnly = true)
    public Optional<ModelResponse> getModelById(Long id) {
        logger.info("Fetching evaluation model with id: {}", id);
        Optional<EvaluationModel> model = modelRepository.findByIdWithParameters(id);
        return model.map(this::convertToResponse);
    }
    
    /**
     * Create new evaluation model
     */
    public ModelResponse createModel(ModelRequest request) {
        logger.info("Creating new evaluation model: {}", request.getName());
        
        // Validate total weight equals 100
        validateTotalWeight(request.getParameters());
        
        // Check if model name already exists
        if (modelRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("评价模型名称已存在: " + request.getName());
        }
        
        // Create model
        EvaluationModel model = new EvaluationModel(request.getName());
        model = modelRepository.save(model);
        
        // Create parameters
        List<EvaluationParameter> parameters = createParameters(model, request.getParameters());
        model.setParameters(parameters);
        
        logger.info("Successfully created evaluation model with id: {}", model.getId());
        return convertToResponse(model);
    }
    
    /**
     * Update evaluation model
     */
    public ModelResponse updateModel(Long id, ModelRequest request) {
        logger.info("Updating evaluation model with id: {}", id);
        
        // Validate total weight equals 100
        validateTotalWeight(request.getParameters());
        
        // Find existing model
        EvaluationModel model = modelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("评价模型不存在: " + id));
        
        // Check if name is being changed and if new name already exists
        if (!model.getName().equals(request.getName()) && 
            modelRepository.findByName(request.getName()).isPresent()) {
            throw new IllegalArgumentException("评价模型名称已存在: " + request.getName());
        }
        
        // Update model name
        model.setName(request.getName());
        
        // Delete existing parameters
        parameterRepository.deleteByModelId(id);
        
        // Create new parameters
        List<EvaluationParameter> parameters = createParameters(model, request.getParameters());
        model.setParameters(parameters);
        
        model = modelRepository.save(model);
        
        logger.info("Successfully updated evaluation model with id: {}", id);
        return convertToResponse(model);
    }
    
    /**
     * Delete evaluation model
     */
    public void deleteModel(Long id) {
        logger.info("Deleting evaluation model with id: {}", id);
        
        // Check if model exists
        if (!modelRepository.existsById(id)) {
            throw new IllegalArgumentException("评价模型不存在: " + id);
        }
        
        // Check if model is used by competitions
        if (modelRepository.isModelUsedByCompetitions(id)) {
            throw new IllegalStateException("无法删除正在使用的评价模型: " + id);
        }
        
        modelRepository.deleteById(id);
        logger.info("Successfully deleted evaluation model with id: {}", id);
    }
    
    /**
     * Initialize preset "芒果" evaluation model if it doesn't exist
     */
    @Transactional
    public void initializePresetModels() {
        logger.info("Initializing preset evaluation models");
        
        // Check if "芒果" model already exists
        if (modelRepository.findByName("芒果").isEmpty()) {
            logger.info("Creating preset '芒果' evaluation model");
            
            ModelRequest mangoModel = new ModelRequest();
            mangoModel.setName("芒果");
            mangoModel.setParameters(List.of(
                new ModelRequest.ParameterRequest("外观", 10),
                new ModelRequest.ParameterRequest("风味", 24),
                new ModelRequest.ParameterRequest("滋味", 16),
                new ModelRequest.ParameterRequest("质构", 18),
                new ModelRequest.ParameterRequest("形状", 22),
                new ModelRequest.ParameterRequest("营养", 10)
            ));
            
            createModel(mangoModel);
            logger.info("Successfully created preset '芒果' evaluation model");
        } else {
            logger.info("Preset '芒果' evaluation model already exists");
        }
    }
    
    /**
     * Validate that total weight equals 100
     */
    private void validateTotalWeight(List<ModelRequest.ParameterRequest> parameters) {
        int totalWeight = parameters.stream()
                .mapToInt(ModelRequest.ParameterRequest::getWeight)
                .sum();
        
        if (totalWeight != 100) {
            throw new IllegalArgumentException("评价参数总分值必须为100分，当前为: " + totalWeight);
        }
    }
    
    /**
     * Create parameters for a model
     */
    private List<EvaluationParameter> createParameters(EvaluationModel model, 
                                                      List<ModelRequest.ParameterRequest> parameterRequests) {
        return IntStream.range(0, parameterRequests.size())
                .mapToObj(i -> {
                    ModelRequest.ParameterRequest req = parameterRequests.get(i);
                    EvaluationParameter parameter = new EvaluationParameter(req.getName(), req.getWeight(), i + 1);
                    parameter.setModel(model);
                    return parameterRepository.save(parameter);
                })
                .toList();
    }
    
    /**
     * Convert entity to response DTO
     */
    private ModelResponse convertToResponse(EvaluationModel model) {
        List<ModelResponse.ParameterResponse> parameterResponses = model.getParameters() != null ?
                model.getParameters().stream()
                        .map(p -> new ModelResponse.ParameterResponse(p.getId(), p.getName(), p.getWeight(), p.getDisplayOrder()))
                        .toList() :
                List.of();
        
        return new ModelResponse(
                model.getId(),
                model.getName(),
                parameterResponses,
                model.getCreatedAt(),
                model.getUpdatedAt()
        );
    }
}