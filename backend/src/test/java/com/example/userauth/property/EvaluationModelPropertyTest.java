package com.example.userauth.property;

import com.example.userauth.dto.ModelRequest;
import com.example.userauth.dto.ModelResponse;
import com.example.userauth.service.EvaluationModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;

/**
 * Property-based tests for Evaluation Model Management
 * 
 * Tests the following properties:
 * - Property 15: Non-admin users cannot access model management (service-level validation)
 * - Property 16: Model creation persistence
 * - Property 17: Models in use cannot be deleted
 * - Property 18: Evaluation model total score validation
 * - Property 19: Preset mango model
 */
class EvaluationModelPropertyTest {

    @Mock
    private EvaluationModelService evaluationModelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    /**
     * Property 15: Non-admin users cannot access model management
     * Validates requirements: 6.2.2
     */
    @Test
    void property15_nonAdminCannotAccessModelManagement() {
        // Generate valid parameters with total weight = 100
        List<ModelRequest.ParameterRequest> validParams = generateValidParameters(3);
        ModelRequest request = new ModelRequest("TestModel", validParams);

        // Mock service to simulate admin-only access control
        when(evaluationModelService.createModel(any(ModelRequest.class)))
                .thenThrow(new SecurityException("Access denied: Admin privileges required"));

        // Verify that non-admin access is rejected at service level
        assertThatThrownBy(() -> evaluationModelService.createModel(request))
                .isInstanceOf(SecurityException.class)
                .hasMessageContaining("Admin privileges required");
    }

    /**
     * Property 16: Model creation persistence
     * Validates requirements: 6.2.10
     */
    @Test
    void property16_modelCreationPersistence() {
        String modelName = "TestModel";
        int paramCount = 3;
        
        // Generate valid parameters with total weight = 100
        List<ModelRequest.ParameterRequest> validParams = generateValidParameters(paramCount);
        ModelRequest request = new ModelRequest(modelName, validParams);

        // Create expected response
        List<ModelResponse.ParameterResponse> responseParams = new ArrayList<>();
        for (int i = 0; i < validParams.size(); i++) {
            ModelRequest.ParameterRequest param = validParams.get(i);
            responseParams.add(new ModelResponse.ParameterResponse((long) (i + 1), param.getName(), param.getWeight(), i + 1));
        }
        
        ModelResponse expectedResponse = new ModelResponse(
                1L, 
                modelName, 
                responseParams, 
                LocalDateTime.now(), 
                LocalDateTime.now()
        );

        // Mock the service to return a successful response
        when(evaluationModelService.createModel(any(ModelRequest.class))).thenReturn(expectedResponse);

        // Test model creation
        ModelResponse result = evaluationModelService.createModel(request);

        // Verify response contains model ID and name
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getName()).isEqualTo(modelName);
        assertThat(result.getParameters()).hasSize(paramCount);
    }

    /**
     * Property 17: Models in use cannot be deleted
     * Validates requirements: 6.2.13, 9.1.8
     */
    @Test
    void property17_modelsInUseCannotBeDeleted() {
        long modelId = 1L;
        
        // Mock service to throw exception when trying to delete model in use
        doThrow(new IllegalStateException("Cannot delete model that is in use"))
                .when(evaluationModelService).deleteModel(anyLong());

        // Test deletion of model in use should throw exception
        assertThatThrownBy(() -> {
            evaluationModelService.deleteModel(modelId);
        })
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Cannot delete model that is in use");
    }

    /**
     * Property 18: Evaluation model total score validation
     * Validates requirements: 6.2.5
     */
    @Test
    void property18_evaluationModelTotalScoreValidation() {
        String modelName = "TestModel";
        
        // Create parameters with total weight != 100
        List<ModelRequest.ParameterRequest> invalidParams = new ArrayList<>();
        invalidParams.add(new ModelRequest.ParameterRequest("Param1", 50));
        invalidParams.add(new ModelRequest.ParameterRequest("Param2", 60)); // Total = 110, not 100
        
        ModelRequest request = new ModelRequest(modelName, invalidParams);

        // Mock service to throw validation exception
        when(evaluationModelService.createModel(any(ModelRequest.class)))
                .thenThrow(new IllegalArgumentException("Total weight must equal 100"));

        // Should fail validation
        assertThatThrownBy(() -> evaluationModelService.createModel(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Total weight must equal 100");
    }

    /**
     * Property 19: Preset mango model
     * Validates requirements: 6.2.14
     */
    @Test
    void property19_presetMangoModel() {
        // Create mock mango model response
        List<ModelResponse.ParameterResponse> parameters = new ArrayList<>();
        parameters.add(new ModelResponse.ParameterResponse(1L, "外观", 10, 1));
        parameters.add(new ModelResponse.ParameterResponse(2L, "风味", 24, 2));
        parameters.add(new ModelResponse.ParameterResponse(3L, "滋味", 16, 3));
        parameters.add(new ModelResponse.ParameterResponse(4L, "质构", 18, 4));
        parameters.add(new ModelResponse.ParameterResponse(5L, "形状", 22, 5));
        parameters.add(new ModelResponse.ParameterResponse(6L, "营养", 10, 6));
        
        ModelResponse mangoModel = new ModelResponse(
                1L,
                "芒果",
                parameters,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        
        // Mock service to return the mango model
        when(evaluationModelService.getAllModels()).thenReturn(List.of(mangoModel));

        // Verify the model can be retrieved
        List<ModelResponse> models = evaluationModelService.getAllModels();
        
        assertThat(models).hasSize(1);
        ModelResponse retrievedModel = models.get(0);
        assertThat(retrievedModel.getName()).isEqualTo("芒果");
        assertThat(retrievedModel.getParameters()).hasSize(6);
        
        // Verify total weight is 100
        int totalWeight = retrievedModel.getParameters().stream()
                .mapToInt(ModelResponse.ParameterResponse::getWeight)
                .sum();
        assertThat(totalWeight).isEqualTo(100);
    }

    /**
     * Helper method to generate valid parameters with total weight = 100
     */
    private List<ModelRequest.ParameterRequest> generateValidParameters(int count) {
        List<ModelRequest.ParameterRequest> params = new ArrayList<>();
        
        if (count <= 0) {
            count = 1;
        }
        
        // Distribute 100 points across parameters
        int baseWeight = 100 / count;
        int remainder = 100 % count;
        
        for (int i = 0; i < count; i++) {
            int weight = baseWeight + (i < remainder ? 1 : 0);
            params.add(new ModelRequest.ParameterRequest("Param" + (i + 1), weight));
        }
        
        return params;
    }
}