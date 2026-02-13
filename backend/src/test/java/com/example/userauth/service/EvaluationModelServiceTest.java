package com.example.userauth.service;

import com.example.userauth.dto.ModelRequest;
import com.example.userauth.dto.ModelResponse;
import com.example.userauth.entity.EvaluationModel;
import com.example.userauth.entity.EvaluationParameter;
import com.example.userauth.repository.EvaluationModelRepository;
import com.example.userauth.repository.EvaluationParameterRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("EvaluationModelService Tests")
class EvaluationModelServiceTest {

    @Mock
    private EvaluationModelRepository modelRepository;

    @Mock
    private EvaluationParameterRepository parameterRepository;

    @InjectMocks
    private EvaluationModelService modelService;

    private EvaluationModel model;
    private EvaluationModel model2;
    private EvaluationParameter parameter1;
    private EvaluationParameter parameter2;

    @BeforeEach
    void setUp() {
        model = new EvaluationModel();
        model.setId(1L);
        model.setName("测试模型");
        model.setCreatedAt(LocalDateTime.now());
        model.setUpdatedAt(LocalDateTime.now());

        model2 = new EvaluationModel();
        model2.setId(2L);
        model2.setName("测试模型2");
        model2.setCreatedAt(LocalDateTime.now());
        model2.setUpdatedAt(LocalDateTime.now());

        parameter1 = new EvaluationParameter();
        parameter1.setId(1L);
        parameter1.setName("参数1");
        parameter1.setWeight(50);
        parameter1.setDisplayOrder(1);
        parameter1.setModel(model);

        parameter2 = new EvaluationParameter();
        parameter2.setId(2L);
        parameter2.setName("参数2");
        parameter2.setWeight(50);
        parameter2.setDisplayOrder(2);
        parameter2.setModel(model);

        model.setParameters(Arrays.asList(parameter1, parameter2));
    }

    @Test
    @DisplayName("Should get all models successfully")
    void getAllModels_Success() {
        when(modelRepository.findAllWithParameters()).thenReturn(Arrays.asList(model, model2));

        List<ModelResponse> responses = modelService.getAllModels();

        assertNotNull(responses);
        assertEquals(2, responses.size());
        assertEquals("测试模型", responses.get(0).getName());
        verify(modelRepository).findAllWithParameters();
    }

    @Test
    @DisplayName("Should return empty list when no models exist")
    void getAllModels_Empty() {
        when(modelRepository.findAllWithParameters()).thenReturn(Collections.emptyList());

        List<ModelResponse> responses = modelService.getAllModels();

        assertNotNull(responses);
        assertTrue(responses.isEmpty());
    }

    @Test
    @DisplayName("Should get model by id successfully")
    void getModelById_Success() {
        when(modelRepository.findByIdWithParameters(1L)).thenReturn(Optional.of(model));

        Optional<ModelResponse> response = modelService.getModelById(1L);

        assertTrue(response.isPresent());
        assertEquals("测试模型", response.get().getName());
        assertEquals(2, response.get().getParameters().size());
        verify(modelRepository).findByIdWithParameters(1L);
    }

    @Test
    @DisplayName("Should return empty when model not found")
    void getModelById_NotFound() {
        when(modelRepository.findByIdWithParameters(99L)).thenReturn(Optional.empty());

        Optional<ModelResponse> response = modelService.getModelById(99L);

        assertFalse(response.isPresent());
    }

    @Test
    @DisplayName("Should create model successfully")
    void createModel_Success() {
        ModelRequest request = createValidModelRequest();

        when(modelRepository.findByName("测试模型")).thenReturn(Optional.empty());
        when(modelRepository.save(any(EvaluationModel.class))).thenReturn(model);
        when(parameterRepository.save(any(EvaluationParameter.class))).thenReturn(parameter1, parameter2);

        ModelResponse response = modelService.createModel(request);

        assertNotNull(response);
        assertEquals("测试模型", response.getName());
        verify(modelRepository).findByName("测试模型");
        verify(modelRepository).save(any(EvaluationModel.class));
    }

    @Test
    @DisplayName("Should throw exception when model name already exists")
    void createModel_DuplicateName() {
        ModelRequest request = createValidModelRequest();

        when(modelRepository.findByName("测试模型")).thenReturn(Optional.of(model));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> modelService.createModel(request));
        assertEquals("评价模型名称已存在: 测试模型", exception.getMessage());
    }

    @Test
    @DisplayName("Should throw exception when total weight is not 100")
    void createModel_InvalidWeight() {
        ModelRequest request = createValidModelRequest();
        request.getParameters().get(0).setWeight(30);
        request.getParameters().get(1).setWeight(40);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> modelService.createModel(request));
        assertTrue(exception.getMessage().contains("评价参数总分值必须为100分"));
    }

    @Test
    @DisplayName("Should update model successfully")
    void updateModel_Success() {
        ModelRequest request = createValidModelRequest();
        request.setName("更新后的模型");

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(modelRepository.findByName("更新后的模型")).thenReturn(Optional.empty());
        when(parameterRepository.save(any(EvaluationParameter.class))).thenReturn(parameter1, parameter2);
        when(modelRepository.save(any(EvaluationModel.class))).thenReturn(model);

        ModelResponse response = modelService.updateModel(1L, request);

        assertNotNull(response);
        verify(modelRepository).findById(1L);
        verify(parameterRepository).deleteByModelId(1L);
        verify(modelRepository).save(any(EvaluationModel.class));
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent model")
    void updateModel_NotFound() {
        ModelRequest request = createValidModelRequest();

        when(modelRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> modelService.updateModel(99L, request));
        assertTrue(exception.getMessage().contains("评价模型不存在"));
    }

    @Test
    @DisplayName("Should throw exception when new name already exists")
    void updateModel_DuplicateName() {
        ModelRequest request = createValidModelRequest();
        request.setName("测试模型2");

        when(modelRepository.findById(1L)).thenReturn(Optional.of(model));
        when(modelRepository.findByName("测试模型2")).thenReturn(Optional.of(model2));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> modelService.updateModel(1L, request));
        assertEquals("评价模型名称已存在: 测试模型2", exception.getMessage());
    }

    @Test
    @DisplayName("Should delete model successfully")
    void deleteModel_Success() {
        when(modelRepository.existsById(1L)).thenReturn(true);
        when(modelRepository.isModelUsedByCompetitions(1L)).thenReturn(false);

        modelService.deleteModel(1L);

        verify(modelRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Should throw exception when deleting non-existent model")
    void deleteModel_NotFound() {
        when(modelRepository.existsById(99L)).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> modelService.deleteModel(99L));
        assertTrue(exception.getMessage().contains("评价模型不存在"));
    }

    @Test
    @DisplayName("Should throw exception when model is in use")
    void deleteModel_InUse() {
        when(modelRepository.existsById(1L)).thenReturn(true);
        when(modelRepository.isModelUsedByCompetitions(1L)).thenReturn(true);

        IllegalStateException exception = assertThrows(IllegalStateException.class,
                () -> modelService.deleteModel(1L));
        assertTrue(exception.getMessage().contains("无法删除正在使用的评价模型"));
    }

    @Test
    @DisplayName("Should initialize preset mango model")
    void initializePresetModels_Success() {
        when(modelRepository.findByName("芒果")).thenReturn(Optional.empty());
        when(modelRepository.save(any(EvaluationModel.class))).thenReturn(model);
        when(parameterRepository.save(any(EvaluationParameter.class))).thenReturn(parameter1, parameter2);

        modelService.initializePresetModels();

        verify(modelRepository, atLeast(1)).save(any(EvaluationModel.class));
    }

    @Test
    @DisplayName("Should skip initialization when preset model exists")
    void initializePresetModels_AlreadyExists() {
        when(modelRepository.findByName("芒果")).thenReturn(Optional.of(model));

        modelService.initializePresetModels();

        verify(modelRepository, never()).save(any(EvaluationModel.class));
    }

    private ModelRequest createValidModelRequest() {
        ModelRequest request = new ModelRequest();
        request.setName("测试模型");
        request.setParameters(Arrays.asList(
                new ModelRequest.ParameterRequest("参数1", 50),
                new ModelRequest.ParameterRequest("参数2", 50)
        ));
        return request;
    }
}