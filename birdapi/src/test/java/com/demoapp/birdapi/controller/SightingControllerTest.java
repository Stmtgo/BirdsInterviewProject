package com.demoapp.birdapi.controller;

import com.demoapp.birdapi.dto.SightingDTO;
import com.demoapp.birdapi.exception.ResourceNotFoundException;
import com.demoapp.birdapi.service.SightingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SightingController.class)
class SightingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private SightingService sightingService;

    @Test
    void createSighting_withValidData_shouldReturn201() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO requestDTO = new SightingDTO();
        requestDTO.setBirdId(1L);
        requestDTO.setLocation("Central Park");
        requestDTO.setDateTime(dateTime);

        SightingDTO responseDTO = new SightingDTO();
        responseDTO.setId(100L);
        responseDTO.setBirdId(1L);
        responseDTO.setLocation("Central Park");
        responseDTO.setDateTime(dateTime);

        when(sightingService.createSighting(any(SightingDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(post("/api/sightings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.birdId", is(1)))
                .andExpect(jsonPath("$.location", is("Central Park")));

        verify(sightingService).createSighting(any(SightingDTO.class));
    }

    @Test
    void createSighting_withInvalidData_shouldReturn400() throws Exception {
        // Arrange - missing required fields
        SightingDTO invalidDTO = new SightingDTO();
        invalidDTO.setLocation(""); // blank location

        // Act & Assert
        mockMvc.perform(post("/api/sightings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDTO)))
                .andExpect(status().isBadRequest());

        verify(sightingService, never()).createSighting(any());
    }

    @Test
    void getSightingById_whenExists_shouldReturn200() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO sightingDTO = new SightingDTO();
        sightingDTO.setId(100L);
        sightingDTO.setBirdId(1L);
        sightingDTO.setLocation("Central Park");
        sightingDTO.setDateTime(dateTime);

        when(sightingService.getSightingById(100L)).thenReturn(sightingDTO);

        // Act & Assert
        mockMvc.perform(get("/api/sightings/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.location", is("Central Park")));

        verify(sightingService).getSightingById(100L);
    }

    @Test
    void getSightingById_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        when(sightingService.getSightingById(999L))
                .thenThrow(new ResourceNotFoundException("Sighting", 999L));

        // Act & Assert
        mockMvc.perform(get("/api/sightings/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message").exists());

        verify(sightingService).getSightingById(999L);
    }

    @Test
    void getAllSightings_shouldReturnPagedResults() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO sighting1 = new SightingDTO();
        sighting1.setId(100L);
        sighting1.setBirdId(1L);
        sighting1.setLocation("Central Park");
        sighting1.setDateTime(dateTime);

        SightingDTO sighting2 = new SightingDTO();
        sighting2.setId(101L);
        sighting2.setBirdId(2L);
        sighting2.setLocation("Lake View");
        sighting2.setDateTime(dateTime.plusHours(1));

        Pageable pageable = PageRequest.of(0, 5);
        Page<SightingDTO> page = new PageImpl<>(Arrays.asList(sighting1, sighting2), pageable, 2);

        when(sightingService.getAllSightings(any(Pageable.class))).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/sightings")
                        .param("page", "0")
                        .param("size", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].location", is("Central Park")))
                .andExpect(jsonPath("$.content[1].location", is("Lake View")))
                .andExpect(jsonPath("$.totalElements", is(2)));

        verify(sightingService).getAllSightings(any(Pageable.class));
    }

    @Test
    void updateSighting_withValidData_shouldReturn200() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO updateDTO = new SightingDTO();
        updateDTO.setBirdId(1L);
        updateDTO.setLocation("Updated Location");
        updateDTO.setDateTime(dateTime);

        SightingDTO responseDTO = new SightingDTO();
        responseDTO.setId(100L);
        responseDTO.setBirdId(1L);
        responseDTO.setLocation("Updated Location");
        responseDTO.setDateTime(dateTime);

        when(sightingService.updateSighting(eq(100L), any(SightingDTO.class))).thenReturn(responseDTO);

        // Act & Assert
        mockMvc.perform(put("/api/sightings/100")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(100)))
                .andExpect(jsonPath("$.location", is("Updated Location")));

        verify(sightingService).updateSighting(eq(100L), any(SightingDTO.class));
    }

    @Test
    void updateSighting_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO updateDTO = new SightingDTO();
        updateDTO.setBirdId(1L);
        updateDTO.setLocation("Updated Location");
        updateDTO.setDateTime(dateTime);

        when(sightingService.updateSighting(eq(999L), any(SightingDTO.class)))
                .thenThrow(new ResourceNotFoundException("Sighting", 999L));

        // Act & Assert
        mockMvc.perform(put("/api/sightings/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isNotFound());

        verify(sightingService).updateSighting(eq(999L), any(SightingDTO.class));
    }

    @Test
    void deleteSighting_whenExists_shouldReturn204() throws Exception {
        // Arrange
        doNothing().when(sightingService).deleteSighting(100L);

        // Act & Assert
        mockMvc.perform(delete("/api/sightings/100"))
                .andExpect(status().isNoContent());

        verify(sightingService).deleteSighting(100L);
    }

    @Test
    void deleteSighting_whenNotFound_shouldReturn404() throws Exception {
        // Arrange
        doThrow(new ResourceNotFoundException("Sighting", 999L))
                .when(sightingService).deleteSighting(999L);

        // Act & Assert
        mockMvc.perform(delete("/api/sightings/999"))
                .andExpect(status().isNotFound());

        verify(sightingService).deleteSighting(999L);
    }

    @Test
    void searchSightings_withAllParameters_shouldReturnMatchingResults() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        LocalDateTime fromDate = dateTime.minusDays(1);
        LocalDateTime toDate = dateTime.plusDays(1);

        SightingDTO sightingDTO = new SightingDTO();
        sightingDTO.setId(100L);
        sightingDTO.setBirdId(1L);
        sightingDTO.setLocation("Central Park");
        sightingDTO.setDateTime(dateTime);

        Pageable pageable = PageRequest.of(0, 5);
        Page<SightingDTO> page = new PageImpl<>(Collections.singletonList(sightingDTO), pageable, 1);

        when(sightingService.searchSightings(
                eq("Blue Jay"),
                eq("Park"),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/sightings/search")
                        .param("birdName", "Blue Jay")
                        .param("location", "Park")
                        .param("fromDate", fromDate.toString())
                        .param("toDate", toDate.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].location", is("Central Park")));

        verify(sightingService).searchSightings(
                eq("Blue Jay"),
                eq("Park"),
                any(LocalDateTime.class),
                any(LocalDateTime.class),
                any(Pageable.class)
        );
    }

    @Test
    void searchSightings_withNoParameters_shouldReturnAll() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO sightingDTO = new SightingDTO();
        sightingDTO.setId(100L);
        sightingDTO.setBirdId(1L);
        sightingDTO.setLocation("Central Park");
        sightingDTO.setDateTime(dateTime);

        Pageable pageable = PageRequest.of(0, 5);
        Page<SightingDTO> page = new PageImpl<>(Collections.singletonList(sightingDTO), pageable, 1);

        when(sightingService.searchSightings(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/sightings/search"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

        verify(sightingService).searchSightings(
                isNull(),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }

    @Test
    void searchSightings_withOnlyBirdName_shouldReturnMatchingResults() throws Exception {
        // Arrange
        LocalDateTime dateTime = LocalDateTime.of(2025, 1, 15, 10, 30);
        SightingDTO sightingDTO = new SightingDTO();
        sightingDTO.setId(100L);
        sightingDTO.setBirdId(1L);
        sightingDTO.setLocation("Central Park");
        sightingDTO.setDateTime(dateTime);

        Pageable pageable = PageRequest.of(0, 5);
        Page<SightingDTO> page = new PageImpl<>(Collections.singletonList(sightingDTO), pageable, 1);

        when(sightingService.searchSightings(
                eq("Blue Jay"),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        )).thenReturn(page);

        // Act & Assert
        mockMvc.perform(get("/api/sightings/search")
                        .param("birdName", "Blue Jay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].birdId", is(1)));

        verify(sightingService).searchSightings(
                eq("Blue Jay"),
                isNull(),
                isNull(),
                isNull(),
                any(Pageable.class)
        );
    }
}

