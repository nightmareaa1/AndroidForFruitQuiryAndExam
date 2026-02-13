package com.example.userauth.controller;

import com.example.userauth.dto.ModelRequest;
import com.example.userauth.dto.ModelResponse;
import com.example.userauth.service.EvaluationModelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(EvaluationModelController.class)
@ContextConfiguration(classes = {EvaluationModelController.class, TestConfig.class})
public class EvaluationModelControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private EvaluationModelService evaluationModelService;

    @Test
    void getAllModels_success() throws Exception {
        ModelResponse m = new ModelResponse();
        m.setId(1L);
        m.setName("Test Model");
        ModelResponse.ParameterResponse pr = new ModelResponse.ParameterResponse(1L, "Param1", 10, 0);
        m.setParameters(List.of(pr));
        when(evaluationModelService.getAllModels()).thenReturn(List.of(m));
        mockMvc.perform(get("/api/evaluation-models")).andExpect(status().isOk());
    }

    @Test
    void getModelById_found() throws Exception {
        ModelResponse m = new ModelResponse();
        m.setId(1L);
        m.setName("Test");
        ModelResponse.ParameterResponse pr2 = new ModelResponse.ParameterResponse(1L, "Param1", 10, 0);
        m.setParameters(List.of(pr2));
        when(evaluationModelService.getModelById(1L)).thenReturn(Optional.of(m));
        mockMvc.perform(get("/api/evaluation-models/1")).andExpect(status().isOk());
    }

    @Test
    void getModelById_notFound() throws Exception {
        when(evaluationModelService.getModelById(2L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/evaluation-models/2")).andExpect(status().isNotFound());
    }

    @Test
    void createModel_success() throws Exception {
        ModelRequest req = new ModelRequest();
        req.setName("New Model");
        ModelRequest.ParameterRequest p = new ModelRequest.ParameterRequest("Param1", 10);
        req.setParameters(List.of(p));
        ModelResponse resp = new ModelResponse();
        resp.setId(2L);
        resp.setName("New Model");
        when(evaluationModelService.createModel(any(ModelRequest.class))).thenReturn(resp);
        mockMvc.perform(post("/api/evaluation-models").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isCreated());
    }

    @Test
    void createModel_invalidArg() throws Exception {
        when(evaluationModelService.createModel(any(ModelRequest.class))).thenThrow(new IllegalArgumentException("Bad"));
        ModelRequest req = new ModelRequest();
        req.setName("Bad");
        mockMvc.perform(post("/api/evaluation-models").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isBadRequest());
    }

        @Test
    void updateModel_success() throws Exception {
        ModelRequest req = new ModelRequest();
        req.setName("Updated");
        // Provide parameters to satisfy validation rules in ModelRequest
        ModelRequest.ParameterRequest p = new ModelRequest.ParameterRequest("Param1", 10);
        req.setParameters(List.of(p));
        ModelResponse resp = new ModelResponse();
        resp.setId(1L);
        resp.setName("Updated");
        when(evaluationModelService.updateModel(eq(1L), any(ModelRequest.class))).thenReturn(resp);
        mockMvc.perform(put("/api/evaluation-models/1").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req))).andExpect(status().isOk());
    }

    @Test
    void deleteModel_success() throws Exception {
        Mockito.doNothing().when(evaluationModelService).deleteModel(1L);
        mockMvc.perform(delete("/api/evaluation-models/1")).andExpect(status().isNoContent());
    }
}
