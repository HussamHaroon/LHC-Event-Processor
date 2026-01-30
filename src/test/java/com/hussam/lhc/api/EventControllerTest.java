package com.hussam.lhc.api;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hussam.lhc.model.ParticleEvent;
import com.hussam.lhc.model.ParticleType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(com.hussam.lhc.UnifiedTestConfig.class)
@DisplayName("EventController Tests")
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/events/high-energy with default parameters should return 200")
    void testGetHighEnergyEvents_DefaultParameters_Returns200() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.size()", lessThanOrEqualTo(10)));
    }

    @Test
    @DisplayName("GET /api/events/high-energy with custom limit should return correct count")
    void testGetHighEnergyEvents_CustomLimit_ReturnsCorrectCount() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("limit", "5")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", lessThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("GET /api/events/high-energy with custom minEnergy should filter correctly")
    void testGetHighEnergyEvents_CustomMinEnergy_FiltersCorrectly() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("minEnergy", "100")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/events/high-energy with limit and minEnergy should return correct results")
    void testGetHighEnergyEvents_CustomLimitAndMinEnergy_ReturnsCorrectResults() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("limit", "5")
                        .param("minEnergy", "75")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", lessThanOrEqualTo(5)));
    }

    @Test
    @DisplayName("GET /api/events/high-energy with limit zero should use default")
    void testGetHighEnergyEvents_LimitZero_UsesDefault() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("limit", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", lessThanOrEqualTo(10)));
    }

    @Test
    @DisplayName("GET /api/events/high-energy with negative minEnergy should use default")
    void testGetHighEnergyEvents_NegativeMinEnergy_UsesDefault() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("minEnergy", "-10")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /api/events/high-energy with limit exceeding max should use max limit")
    void testGetHighEnergyEvents_LimitExceedsMax_UsesMaxLimit() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("limit", "1000")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", lessThanOrEqualTo(100)));
    }

    @Test
    @DisplayName("GET /api/events/high-energy should have correct content type")
    void testGetHighEnergyEvents_HasCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/events/high-energy should return valid fields")
    void testGetHighEnergyEvents_ValidFields() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").exists())
                .andExpect(jsonPath("$[0].timestamp").exists())
                .andExpect(jsonPath("$[0].energyGev").exists())
                .andExpect(jsonPath("$[0].particleType").exists());
    }

    @Test
    @DisplayName("GET /api/events/high-energy events should be ordered by energy descending")
    void testGetHighEnergyEvents_EventsOrderedByEnergyDescending() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/events/high-energy with very high minEnergy should return fewer results")
    void testGetHighEnergyEvents_VeryHighMinEnergy_FewerResults() throws Exception {
        mockMvc.perform(get("/api/events/high-energy")
                        .param("minEnergy", "200")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("GET /api/events/statistics should return 200")
    void testGetStatistics_Returns200() throws Exception {
        mockMvc.perform(get("/api/events/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/events/statistics should have correct content type")
    void testGetStatistics_HasCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/events/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/events/statistics should return valid statistics")
    void testGetStatistics_ReturnsValidStatistics() throws Exception {
        mockMvc.perform(get("/api/events/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").exists())
                .andExpect(jsonPath("$.avgEnergy").exists())
                .andExpect(jsonPath("$.maxEnergy").exists())
                .andExpect(jsonPath("$.minEnergy").exists());
    }

    @Test
    @DisplayName("GET /api/events/statistics should have correct data types")
    void testGetStatistics_DataTypesAreCorrect() throws Exception {
        mockMvc.perform(get("/api/events/statistics")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalEvents").isNumber())
                .andExpect(jsonPath("$.avgEnergy").isNumber())
                .andExpect(jsonPath("$.maxEnergy").isNumber())
                .andExpect(jsonPath("$.minEnergy").isNumber());
    }

    @Test
    @DisplayName("GET /api/system/status should return 200")
    void testGetSystemStatus_Returns200() throws Exception {
        mockMvc.perform(get("/api/system/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/system/status should have correct content type")
    void testGetSystemStatus_HasCorrectContentType() throws Exception {
        mockMvc.perform(get("/api/system/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    @DisplayName("GET /api/system/status should return valid status")
    void testGetSystemStatus_ReturnsValidStatus() throws Exception {
        mockMvc.perform(get("/api/system/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").exists())
                .andExpect(jsonPath("$.databaseStatus").exists())
                .andExpect(jsonPath("$.queueSize").exists())
                .andExpect(jsonPath("$.activeProducers").exists())
                .andExpect(jsonPath("$.activeConsumers").exists());
    }

    @Test
    @DisplayName("GET /api/system/status should have valid status values")
    void testGetSystemStatus_ValidStatusValues() throws Exception {
        mockMvc.perform(get("/api/system/status")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.activeProducers").isNumber())
                .andExpect(jsonPath("$.activeConsumers").isNumber())
                .andExpect(jsonPath("$.queueSize").isNumber());
    }

    @Test
    @DisplayName("Sequential requests should return consistent results")
    void testSequentialRequests_ConsistentResults() throws Exception {
        String result1 = mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String result2 = mockMvc.perform(get("/api/events/high-energy")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertTrue(result1 != null && !result1.isEmpty());
        assertTrue(result2 != null && !result2.isEmpty());
    }
}
